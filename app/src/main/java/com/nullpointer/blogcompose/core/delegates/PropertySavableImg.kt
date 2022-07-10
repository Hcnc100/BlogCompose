package com.nullpointer.blogcompose.core.delegates

import android.content.Context
import android.net.Uri
import androidx.annotation.StringRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import androidx.lifecycle.SavedStateHandle
import kotlinx.coroutines.*
import me.shouheng.compress.Compress
import me.shouheng.compress.concrete
import me.shouheng.compress.strategy.config.ScaleMode
import timber.log.Timber

class PropertySavableImg(
    state: SavedStateHandle,
    @StringRes
    private val errorCompressImg: Int,
    private val scope: CoroutineScope,
    private val actionSendError: (errorMsg: Int) -> Unit
) {

    companion object {
        private const val INIT_ID = 1
        private const val FIN_ID = 1234567890
    }

    private val idSaved = "SAVED_PROPERTY_IMG_${(INIT_ID..FIN_ID).random()}"

    var value: Uri by SavableComposeState(state, "$idSaved-value", Uri.EMPTY)
        private set

    private var jobCompress: Job? = null

    var isCompress by mutableStateOf(false)
        private set

    private var initValue by SavableProperty(state, "$idSaved-initValue", Uri.EMPTY)

    val isEmpty get() = value == Uri.EMPTY

    val hasChanged get() = value != initValue

    fun initValue(value: Uri) {
        this.value = value
        initValue = value
    }

    fun changeValue(newValue: Uri, context: Context) {
        jobCompress?.cancel()
        jobCompress = scope.launch {
            try {
                isCompress = true
                val bitmapCompress = Compress.with(context, newValue)
                    .setQuality(70)
                    .concrete {
                        withMaxHeight(500f)
                        withMaxWidth(500f)
                        withScaleMode(ScaleMode.SCALE_SMALLER)
                        withIgnoreIfSmaller(true)
                    }.get(Dispatchers.IO)
                value = bitmapCompress.toUri()
            } catch (e: Exception) {
                when (e) {
                    is CancellationException -> throw e
                    else -> {
                        Timber.e("Job compress exception $e")
                        value = initValue
                        actionSendError(errorCompressImg)
                    }
                }
            } finally {
                isCompress = false
            }
        }
    }

    fun clearValue() {
        isCompress = false
        value = Uri.EMPTY
        initValue = Uri.EMPTY
        jobCompress?.cancel()
    }
}