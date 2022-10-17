package com.nullpointer.blogcompose.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nullpointer.blogcompose.core.delegates.SavableProperty
import com.nullpointer.blogcompose.core.states.Resource
import com.nullpointer.blogcompose.core.utils.ExceptionManager.sendMessageErrorToException
import com.nullpointer.blogcompose.core.utils.launchSafeIO
import com.nullpointer.blogcompose.domain.post.PostRepository
import com.nullpointer.blogcompose.domain.services.ServicesRepository
import com.nullpointer.blogcompose.models.posts.Post
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(
    state: SavedStateHandle,
    private val postRepository: PostRepository,
    private val servicesRepository: ServicesRepository
) : ViewModel() {

    companion object {
        private const val CONCATENATE_ENABLE = "KEY_CONCATENATE_ENABLE_POST"
    }

    private var isConcatenateEnable by SavableProperty(state, CONCATENATE_ENABLE, true)

    private val _messagePost = Channel<String>()
    val messagePost = _messagePost.receiveAsFlow()

    var isRequestData by mutableStateOf(false)
        private set

    var isConcatenatePost by mutableStateOf(false)
        private set

    val eventUploadPost get() = servicesRepository.finishUploadSuccessEvent

    val listPost = postRepository.listLastPost.transform<List<Post>, Resource<List<Post>>> {
        isConcatenateEnable = true
        emit(Resource.Success(it))
    }.catch {
        sendMessageErrorToException(
            exception = it,
            message = "Error to get list of posts",
            channel = _messagePost
        )
        emit(Resource.Failure)
    }.flowOn(Dispatchers.IO).stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        Resource.Loading
    )

    init {
        // * when init this view model , request new post if is needed
        Timber.e("Se inicio el post view model")
        requestNewPost()
    }


    fun requestNewPost(forceRefresh: Boolean = false) = launchSafeIO(
        isEnabled = !isRequestData,
        blockBefore = { isRequestData = true },
        blockAfter = { isRequestData = false },
        blockIO = {
            val sizeNewPost = postRepository.requestLastPost(forceRefresh)
            Timber.d("were obtained $sizeNewPost new post")
        },
        blockException = {
            sendMessageErrorToException(
                exception = it,
                message = "Error to request new post",
                channel = _messagePost
            )
        })

    fun concatenatePost(callbackSuccess: () -> Unit) = launchSafeIO(
        blockBefore = { isConcatenatePost = true },
        blockAfter = { isConcatenatePost = false },
        blockException = {
            sendMessageErrorToException(
                exception = it,
                message = "Error to concatenate post",
                channel = _messagePost
            )
        },
        blockIO = {
            val sizeConcat = postRepository.concatenatePost()
            Timber.d("New post concatenate $sizeConcat")
            withContext(Dispatchers.Main) {
                if (sizeConcat == 0) isConcatenateEnable = false else callbackSuccess()
            }

        }
    )


    fun deleterPostInvalid(idPost: String) = launchSafeIO {
//        postRepository.deleterPost(idPost)
    }
}