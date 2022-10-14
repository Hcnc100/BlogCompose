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
import com.nullpointer.blogcompose.models.posts.MyPost
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MyPostViewModel @Inject constructor(
    private val postRepository: PostRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        private const val CONCATENATE_ENABLE = "KEY_CONCATENATE_ENABLE_MY_POST"
    }

    private var isConcatenateEnable by SavableProperty(savedStateHandle, CONCATENATE_ENABLE, false)

    private val _messageMyPosts = Channel<String>()
    val messageMyPosts = _messageMyPosts.receiveAsFlow()

    var isRequestMyPost by mutableStateOf(false)
        private set

    var isConcatMyPost by mutableStateOf(false)
        private set


    val listMyPost = postRepository.listMyLastPost.transform<List<MyPost>, Resource<List<MyPost>>> {
        isConcatenateEnable = true
        emit(Resource.Success(it))
    }.catch {
        sendMessageErrorToException(
            channel = _messageMyPosts,
            message = "Error get my list post",
            exception = it
        )
        emit(Resource.Failure)
    }.flowOn(Dispatchers.IO).stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        Resource.Loading
    )

    init {
        requestNewPost()
    }


    fun requestNewPost(forceRefresh: Boolean = false) = launchSafeIO(
        isEnabled = !isRequestMyPost,
        blockBefore = { isRequestMyPost = true },
        blockAfter = { isRequestMyPost = false },
        blockException = {
            sendMessageErrorToException(
                exception = it,
                message = "Error request new my post",
                channel = _messageMyPosts
            )
        },
        blockIO = {
            val sizeNewPost = postRepository.requestMyLastPost(forceRefresh)
            if (sizeNewPost > 0) isConcatenateEnable = true
            Timber.d("get $sizeNewPost my post news")
        }
    )

    fun concatenatePost(callbackSuccess: () -> Unit) = launchSafeIO(
        isEnabled = !isConcatMyPost && isConcatenateEnable,
        blockBefore = { isConcatMyPost = true },
        blockAfter = { isConcatMyPost = false },
        blockException = {
            sendMessageErrorToException(
                exception = it,
                message = "Error concatenate my post",
                channel = _messageMyPosts
            )
        },
        blockIO = {
            val countPost = postRepository.concatenateMyPost()
            Timber.d("number concat my post $countPost")
            withContext(Dispatchers.Main) {
                if (countPost == 0) isConcatenateEnable = false else callbackSuccess()
            }
        }
    )
}