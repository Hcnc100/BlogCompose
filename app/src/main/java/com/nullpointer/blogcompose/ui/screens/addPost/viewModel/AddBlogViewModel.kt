package com.nullpointer.blogcompose.ui.screens.addPost.viewModel

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nullpointer.blogcompose.R
import com.nullpointer.blogcompose.core.delegates.PropertySavableImg
import com.nullpointer.blogcompose.core.delegates.PropertySavableString
import com.nullpointer.blogcompose.models.posts.Post
import com.nullpointer.blogcompose.services.uploadImg.UploadDataControl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
class AddBlogViewModel @Inject constructor(
    state: SavedStateHandle,
) : ViewModel() {

    companion object {
        private const val MAX_SIZE_DESCRIPTION = 250
    }

    private val _messageAddBlog = Channel<Int>()
    val messageAddBlog = _messageAddBlog.receiveAsFlow()

    val imageBlog = PropertySavableImg(
        state = state,
        errorCompressImg = R.string.message_error_compress_img,
        scope = viewModelScope,
        actionSendError = _messageAddBlog::trySend
    )

    val description = PropertySavableString(
        state = state,
        label = R.string.text_label_description_post,
        hint = R.string.text_hint_description,
        maxLength = MAX_SIZE_DESCRIPTION,
        emptyError = R.string.error_empty_description,
        lengthError = R.string.error_length_description
    )

    val isValidData get() = !imageBlog.isEmpty && !description.hasError

    fun getPostValidate(context: Context):Boolean{
        return if (isValidData){
            UploadDataControl.startServicesUploadPost(context,description.value,imageBlog.value)
            true
        }else false
    }

}