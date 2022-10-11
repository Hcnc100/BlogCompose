package com.nullpointer.blogcompose.core.delegates


import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import kotlinx.coroutines.*
import timber.log.Timber

class PropertySavableImg(
    tagSavable: String,
    state: SavedStateHandle,
    private val scope: CoroutineScope,
    private val defaultValue: Uri = Uri.EMPTY,
    private val actionSendErrorCompress: () -> Unit,
    private val actionCompress: suspend (Uri) -> Uri
) {

    private val idSaved = "SAVED_PROPERTY_IMG_$tagSavable"

    var value: Uri by SavableComposeState(state, "$idSaved-CURRENT-VALUE", Uri.EMPTY)
        private set

    var isCompress by mutableStateOf(false)
        private set

    private var jobCompress: Job? = null

    val hasChanged get() = value != defaultValue

    val isNotEmpty get() = value != Uri.EMPTY

    val isEmpty get() = !isNotEmpty

    var hasError = false

    fun changeValue(newValue: Uri, isInit: Boolean = false) {
        if (isInit) {
            value = newValue
        } else {
            jobCompress?.cancel()
            jobCompress = scope.launch {
                try {
                    isCompress = true
                    value = withContext(Dispatchers.IO) { actionCompress(newValue) }
                    hasError = false
                } catch (e: Exception) {
                    when (e) {
                        is CancellationException -> throw e
                        else -> {
                            Timber.e("Job compress exception $e")
                            value = defaultValue
                            hasError = true
                            actionSendErrorCompress()
                        }
                    }
                } finally {
                    isCompress = false
                }
            }
        }
    }

    fun reValueField() {
        if (isEmpty) hasError = true
    }

    fun clearValue() {
        isCompress = false
        value = defaultValue
        jobCompress?.cancel()
    }
}