package com.nullpointer.blogcompose.presentation

import androidx.lifecycle.ViewModel
import com.nullpointer.blogcompose.core.utils.ExceptionManager.sendMessageErrorToException
import com.nullpointer.blogcompose.core.utils.launchSafeIO
import com.nullpointer.blogcompose.domain.post.PostRepository
import com.nullpointer.blogcompose.models.posts.SimplePost
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
class LikeViewModel @Inject constructor(
    private val postRepository: PostRepository,
) : ViewModel() {

    private val _messageLike = Channel<String>()
    val messageLike = _messageLike.receiveAsFlow()

    // * var to save job, to like
    private var jobLike: Job? = null


    fun likePost(simplePost: SimplePost) {
        // * this init like job, update the database with new data with the new data of
        // * server
        jobLike?.cancel()
        jobLike = launchSafeIO(
            blockIO = { postRepository.updateLikePost(simplePost, !simplePost.ownerLike) },
            blockException = { exception ->
                sendMessageErrorToException(exception, "Error like post $simplePost", _messageLike)
            }
        )
    }
}