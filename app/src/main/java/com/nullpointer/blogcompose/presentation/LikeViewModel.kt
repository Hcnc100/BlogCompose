package com.nullpointer.blogcompose.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nullpointer.blogcompose.R
import com.nullpointer.blogcompose.core.utils.NetworkException
import com.nullpointer.blogcompose.domain.post.PostRepoImpl
import com.nullpointer.blogcompose.models.posts.SimplePost
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class LikeViewModel @Inject constructor(
    private val postRepo: PostRepoImpl,
) : ViewModel() {

    private val _messageLike = Channel<Int>()
    val messageLike = _messageLike.receiveAsFlow()

    // * var to save job, to like
    private var jobLike: Job? = null


    fun likePost(simplePost: SimplePost) {
        // * this init like job, update the database with new data with the new data of
        // * server
        jobLike?.cancel()
        jobLike = viewModelScope.launch(Dispatchers.IO) {
            try {
                postRepo.updateLikePost(simplePost)
            } catch (e: Exception) {
                when (e) {
                    is CancellationException -> throw e
                    is NetworkException -> _messageLike.send(R.string.message_error_like)
                    else -> {
                        Timber.e("Error al dar like $e")
                        _messageLike.send(R.string.message_error_unknown)
                    }
                }
            }
        }
    }
}