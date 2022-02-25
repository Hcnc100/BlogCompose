package com.nullpointer.blogcompose.ui.screens.addPost.viewModel

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nullpointer.blogcompose.R
import com.nullpointer.blogcompose.core.delegates.SaveableComposeState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import me.shouheng.compress.Compress
import me.shouheng.compress.concrete
import me.shouheng.compress.strategy.config.ScaleMode
import timber.log.Timber
import java.io.File
import javax.inject.Inject

@HiltViewModel
class AddBlogViewModel @Inject constructor(
    state: SavedStateHandle,
) : ViewModel() {
    companion object {
        const val KEY_FILE_IMG = "KEY_FILE_IMG"
        const val KEY_DESCRIPTION = "KEY_DESCRIPTION"
        const val KEY_HAS_ERROR_DESC = "KEY_HAS_ERROR_DESC"
        const val KEY_HAS_ERROR_IMG = "KEY_HAS_ERROR_IMG"
        const val MAX_LENGTH_DESCRIPTION = 250
    }

    var fileImg: File? by SaveableComposeState(state, KEY_FILE_IMG, null)
        private set

    var errorImage: Int by SaveableComposeState(state, KEY_HAS_ERROR_IMG, 0)
        private set

    var description: String by SaveableComposeState(state, KEY_DESCRIPTION, "")
        private set

    var errorDescription: Int by SaveableComposeState(state, KEY_HAS_ERROR_DESC, 0)
        private set

    private var jobCompress: Job? = null
    var isCompress = mutableStateOf(false)
        private set

    fun changeDescription(newDescription: String) {
        errorDescription = when {
            newDescription.length > MAX_LENGTH_DESCRIPTION -> R.string.error_length_description
            newDescription.isEmpty() -> R.string.error_empty_description
            else -> 0
        }
        description = newDescription
    }

    fun validate(): Boolean {
        changeDescription(description)
        if (fileImg == null) errorImage = R.string.error_empty_image
        return errorDescription == 0 && errorImage == 0
    }

    fun changeFileImg(uri: Uri, context: Context) {
        jobCompress?.cancel()
        jobCompress = viewModelScope.launch {
            Timber.d("Init compress")
            isCompress.value = true
            val bitmapCompress = Compress.with(context, uri).setQuality(70).concrete {
                withMaxHeight(500f)
                withMaxWidth(500f)
                withScaleMode(ScaleMode.SCALE_SMALLER)
                withIgnoreIfSmaller(true)
            }.get(Dispatchers.IO)
            fileImg = bitmapCompress
            Timber.d("finish compress")
            isCompress.value = false
            errorImage=0
        }
    }
}