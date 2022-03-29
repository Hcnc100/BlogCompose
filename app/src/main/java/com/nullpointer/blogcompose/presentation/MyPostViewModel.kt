package com.nullpointer.blogcompose.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nullpointer.blogcompose.core.states.Resource
import com.nullpointer.blogcompose.core.utils.NetworkException
import com.nullpointer.blogcompose.domain.post.PostRepoImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

@HiltViewModel
class MyPostViewModel @Inject constructor(
    private val postRepo: PostRepoImpl,
) : ViewModel() {

    private val _messageMyPosts = Channel<String>()
    val messageMyPosts = _messageMyPosts.receiveAsFlow()

    private var jobRequestNew: Job? = null
    private val _stateLoadData = MutableStateFlow<Resource<Unit>>(Resource.Loading())
    val stateLoad = _stateLoadData.asStateFlow()

    private var jobConcatenatePost: Job? = null
    private val _stateConcatenateData = MutableStateFlow<Resource<Unit>>(Resource.Loading())
    val stateConcatenate = _stateConcatenateData.asStateFlow()


    val listMyPost = postRepo.listMyLastPost.catch {
        Timber.e("Error al obtener my post de la base de datos $it")
        _messageMyPosts.trySend("Error deconocido")
    }.flowOn(Dispatchers.IO).stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        emptyList()
    )

    init {
        Timber.e("Se inicio el my repo view model")
        requestNewPost()
    }


    fun requestNewPost(forceRefresh: Boolean = false) {
        // * request last post in cache or if there new post
        // * or force refresh with the argument
        jobRequestNew?.cancel()
        jobRequestNew = viewModelScope.launch(Dispatchers.IO) {
            _stateLoadData.value = Resource.Loading()
            try {
                val sizeNewPost = postRepo.requestMyLastPost(forceRefresh)
                Timber.d("Se obtuvieron $sizeNewPost post nuevos 'mios'")
                _stateLoadData.value = Resource.Success(Unit)
            } catch (e: Exception) {
                _stateLoadData.value = Resource.Failure(e)
                when (e) {
                    is CancellationException -> throw e
                    is NetworkException -> _messageMyPosts.trySend("Verifique su conexion a internet")
                    is NullPointerException -> Timber.e(" Error al obtener ultimas notificaciones El usuario posiblemente es nulo")
                    else -> {
                        _messageMyPosts.trySend("Error desconocido")
                        Timber.e("Error en el request 'myPost' $e")
                    }
                }
            }
        }
    }

    fun concatenatePost() {
        // * request post and concatenate and the last post
        jobConcatenatePost?.cancel()
        jobConcatenatePost = viewModelScope.launch(Dispatchers.IO) {
            _stateConcatenateData.value = Resource.Loading()
            try {
                val sizeRequest = postRepo.concatenateMyPost()
                Timber.d("Post concatenados $sizeRequest")
                _stateConcatenateData.value = Resource.Success(Unit)
            } catch (e: Exception) {
                _stateConcatenateData.value = Resource.Failure(e)
                when (e) {
                    is CancellationException -> throw e
                    is NetworkException -> _messageMyPosts.trySend("Verifique su conexion a internet")
                    else -> {
                        _messageMyPosts.trySend("Error desconocido")
                        Timber.e("Error en el request  de mis post $e")
                    }
                }
            }
        }
    }
}