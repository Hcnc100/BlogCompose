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
import com.nullpointer.blogcompose.domain.post.PostRepoImpl
import com.nullpointer.blogcompose.models.posts.Post
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(
    private val postRepo: PostRepoImpl,
    state: SavedStateHandle
) : ViewModel() {

    companion object {
        private const val KEY_CONCATENATE_ENABLE = "KEY_CONCATENATE_ENABLE_POST"
    }

    private var isConcatenateEnable by SavableProperty(state, KEY_CONCATENATE_ENABLE, true)

    // * state message to show any error or message
    private val _messagePost = Channel<Int>()
    val messagePost = _messagePost.receiveAsFlow()

    // * var to saved the job, to request new data
    // * this for can cancel this work
    private var jobRequestNew: Job? = null
    var stateLoadData by mutableStateOf(false)
        private set


    // * var to save the job, to request post concatenate
    // * this for can cancel this work
    private var jobConcatenatePost: Job? = null
    var stateConcatenateData by mutableStateOf(false)
        private set

    val listPost = flow<Resource<List<Post>>> {
        postRepo.listLastPost.collect {
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
            stateLoadData = true
            try {
                val sizeNewPost =
                    withContext(Dispatchers.IO) { postRepo.requestLastPost(forceRefresh) }
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
                stateLoadData = false
            }
        }
    }

    fun concatenatePost() {
        // * this init new data but, consideration las post saved,
        // * this for concatenate new post and no override the database
        // * launch exception if no there internet
        if (isConcatenateEnable) {
            jobConcatenatePost?.cancel()
            jobConcatenatePost = viewModelScope.launch {
                stateLoadData = true
                try {
                    val sizeConcat = withContext(Dispatchers.IO) { postRepo.concatenatePost() }
                    Timber.d("New post concatenate $sizeConcat")
                    if (sizeConcat == 0) isConcatenateEnable = false

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
                    stateConcatenateData = false
                }
            }
        }

    }
}