package com.nullpointer.blogcompose.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nullpointer.blogcompose.R
import com.nullpointer.blogcompose.core.delegates.SavableProperty
import com.nullpointer.blogcompose.core.states.Resource
import com.nullpointer.blogcompose.core.utils.NetworkException
import com.nullpointer.blogcompose.domain.post.PostRepository
import com.nullpointer.blogcompose.models.posts.Post
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(
    private val postRepository: PostRepository,
    state: SavedStateHandle
) : ViewModel() {

    companion object {
        private const val KEY_CONCATENATE_ENABLE = "KEY_CONCATENATE_ENABLE_POST"
    }

    private var isConcatEnable by SavableProperty(state, KEY_CONCATENATE_ENABLE, true)

    // * state message to show any error or message
    private val _messagePost = Channel<Int>()
    val messagePost = _messagePost.receiveAsFlow()

    // * var to saved the job, to request new data
    // * this for can cancel this work
    private var jobRequestNew: Job? = null
    var stateRequestData by mutableStateOf(false)
        private set


    // * var to save the job, to request post concatenate
    // * this for can cancel this work
    private var jobConcatPost: Job? = null
    var stateConcatData by mutableStateOf(false)
        private set

    val listPost = flow<Resource<List<Post>>> {
        postRepository.listLastPost.collect {
            emit(Resource.Success(it))
        }
    }.catch {
        Timber.d("Error to get list of posts $it")
        _messagePost.trySend(R.string.message_error_unknown)
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


    fun requestNewPost(forceRefresh: Boolean = false) {
        // * this init request for new post, this will from cache or new data server
        // * if no there internet launch exception
        jobRequestNew?.cancel()
        jobRequestNew = viewModelScope.launch {
            stateRequestData = true
            try {
                val sizeNewPost =
                    withContext(Dispatchers.IO) { postRepository.requestLastPost(forceRefresh) }
                Timber.d("were obtained $sizeNewPost new post")
            } catch (e: Exception) {
                when (e) {
                    is CancellationException -> throw e
                    is NetworkException -> _messagePost.trySend(R.string.message_error_internet_checker)
                    else -> {
                        _messagePost.trySend(R.string.message_error_unknown)
                        Timber.e("Error reuqest new post $e")
                    }
                }
            } finally {
                stateRequestData = false
            }
        }
    }

    fun concatenatePost(callbackSuccess: () -> Unit) {
        // * this init new data but, consideration las post saved,
        // * this for concatenate new post and no override the database
        // * launch exception if no there internet
        if (isConcatEnable) {
            jobConcatPost?.cancel()
            jobConcatPost = viewModelScope.launch {
                Timber.d("Init process concatenate post")
                stateConcatData = true
                try {
                    val sizeConcat = withContext(Dispatchers.IO) { postRepository.concatenatePost() }
                    Timber.d("New post concatenate $sizeConcat")
                    if (sizeConcat == 0) isConcatEnable = false else callbackSuccess()

                } catch (e: Exception) {
                    when (e) {
                        is CancellationException -> throw e
                        is NetworkException -> _messagePost.trySend(R.string.message_error_internet_checker)
                        else -> {
                            _messagePost.trySend(R.string.message_error_unknown)
                            Timber.e("Error concatenate new post $e")
                        }
                    }
                } finally {
                    stateConcatData = false
                }
            }
        }
    }

    fun deleterPostInvalid(idPost: String) = viewModelScope.launch(Dispatchers.IO) {
        postRepository.deleterPost(idPost)
    }
}