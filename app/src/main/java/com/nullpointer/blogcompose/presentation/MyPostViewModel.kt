package com.nullpointer.blogcompose.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nullpointer.blogcompose.core.states.Resource
import com.nullpointer.blogcompose.core.utils.NetworkException
import com.nullpointer.blogcompose.domain.post.PostRepoImpl
import com.nullpointer.blogcompose.models.MyPost
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
    private val postRepo: PostRepoImpl
):ViewModel() {

    private val _messagePost = Channel<String>()
    val messagePost = _messagePost.receiveAsFlow()

    private var jobRequestNew: Job? = null
    private val _stateLoadData = MutableStateFlow<Resource<Unit>>(Resource.Loading())
    val stateLoad = _stateLoadData.asStateFlow()

    private var jobConcatenatePost: Job? = null
    private val _stateConcatenateData = MutableStateFlow<Resource<Unit>>(Resource.Loading())
    val stateConcatenate = _stateConcatenateData.asStateFlow()

    val listMyPost = flow<Resource<List<MyPost>>> {
        postRepo.getMyLastPost(false).collect {
            emit(Resource.Success(it))
        }
    }.catch {
        Timber.d("Error al obtener my post de la base de datos $it")
        Resource.Failure<Resource<List<MyPost>>>(Exception(it))
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        Resource.Loading()
    )

    init {
        requestNewPost()
    }


    fun requestNewPost() {
        jobRequestNew?.cancel()
        jobRequestNew = viewModelScope.launch {
            _stateLoadData.value = Resource.Loading()
            try {
                val sizeNewPost = postRepo.requestMyLastPost()
                if (sizeNewPost == 0) {
                    _messagePost.trySend("Es todo, no hay post nuevos")
                } else {
                    _messagePost.trySend("Se obtuvieron $sizeNewPost post nuevos")
                }
                _stateLoadData.value = Resource.Success(Unit)
            } catch (e: Exception) {
                _stateLoadData.value = Resource.Failure(e)
                when (e) {
                    is CancellationException -> throw e
                    is NetworkException -> _messagePost.trySend("Verifique su conexion a internet")
                    else -> {
                        _messagePost.trySend("Error desconocido")
                        Timber.d("Error en el request $e")
                    }
                }
            }
        }
    }

    fun concatenatePost() {
        jobConcatenatePost?.cancel()
        jobConcatenatePost = viewModelScope.launch {
            _stateConcatenateData.value = Resource.Loading()
            try {
                val sizeRequest = postRepo.concatenateMyPost()
                Timber.d("Datos concatenados $sizeRequest")
                _stateConcatenateData.value = Resource.Success(Unit)
            } catch (e: Exception) {
                _stateConcatenateData.value = Resource.Failure(e)
                when (e) {
                    is CancellationException -> throw e
                    is NetworkException -> _messagePost.trySend("Verifique su conexion a internet")
                    else -> {
                        _messagePost.trySend("Error desconocido")
                        Timber.d("Error en el request $e")
                    }
                }
            }
        }
    }

    fun likePost(idPost: String, isLiked: Boolean) = viewModelScope.launch(Dispatchers.IO) {
        try {
            postRepo.updateLikePost(idPost, isLiked)
        } catch (e: Exception) {
            when (e) {
                is CancellationException -> throw e
                is NetworkException -> _messagePost.send("Necesita conexion para esto")
                else -> {
                    Timber.d("Erro al dar like $e")
                    _messagePost.send("Error desconocido")
                }
            }
        }
    }
}