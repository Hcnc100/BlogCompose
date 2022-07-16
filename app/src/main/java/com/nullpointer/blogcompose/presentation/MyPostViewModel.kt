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
import com.nullpointer.blogcompose.domain.post.PostRepository
import com.nullpointer.blogcompose.models.posts.MyPost
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

@HiltViewModel
class MyPostViewModel @Inject constructor(
    private val postRepository: PostRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        private const val KEY_CONCATENATE_ENABLE = "KEY_CONCATENATE_ENABLE_MY_POST"
    }

    private var isConcatenateEnable by SavableProperty(
        savedStateHandle,
        KEY_CONCATENATE_ENABLE, true
    )

    private val _messageMyPosts = Channel<Int>()
    val messageMyPosts = _messageMyPosts.receiveAsFlow()

    private var jobRequestNew: Job? = null
    var stateRequestMyPost by mutableStateOf(false)
        private set

    private var jobConcatMyPost: Job? = null
    var stateConcatMyPost by mutableStateOf(false)
    private set


    val listMyPost = flow<Resource<List<MyPost>>> {
        postRepository.listMyLastPost.collect {
            emit(Resource.Success(it))
        }
    }.catch {
        Timber.e("Error get my post $it")
        _messageMyPosts.trySend(R.string.message_error_unknown)
        emit(Resource.Failure)
    }.flowOn(Dispatchers.IO).stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        Resource.Loading
    )

    init {
        requestNewPost()
    }


    fun requestNewPost(forceRefresh: Boolean = false) {
        // * request last post in cache or if there new post
        // * or force refresh with the argument
        jobRequestNew?.cancel()
        jobRequestNew = viewModelScope.launch {
            stateRequestMyPost = true
            try {
                val sizeNewPost =
                    withContext(Dispatchers.IO) { postRepository.requestMyLastPost(forceRefresh) }
                Timber.d("get $sizeNewPost my post news")
            } catch (e: Exception) {
                when (e) {
                    is CancellationException -> throw e
                    is NetworkException -> _messageMyPosts.trySend(R.string.message_error_internet_checker)
                    is NullPointerException -> Timber.e(" Error al obtener ultimas notificaciones El usuario posiblemente es nulo")
                    else -> {
                        _messageMyPosts.trySend(R.string.message_error_unknown)
                        Timber.e("Error en el request 'myPost' $e")
                    }
                }
            } finally {
                stateRequestMyPost = false
            }
        }
    }

    fun concatenatePost() {
        // * request post and concatenate and the last post
        if (isConcatenateEnable) {
            jobConcatMyPost?.cancel()
            jobConcatMyPost = viewModelScope.launch {
                try {
                    stateConcatMyPost = true
                    val countPost = withContext(Dispatchers.IO) {
                        postRepository.concatenateMyPost()
                    }
                    Timber.d("number concat my post $countPost")
                    if (countPost == 0) isConcatenateEnable = false
                } catch (e: Exception) {
                    when (e) {
                        is CancellationException -> throw e
                        is NetworkException -> _messageMyPosts.trySend(R.string.message_error_internet_checker)
                        else -> {
                            _messageMyPosts.trySend(R.string.message_error_unknown)
                            Timber.e("Error en el request  de mis post $e")
                        }
                    }
                } finally {
                    stateRequestMyPost = false
                }
            }
        }

    }
}