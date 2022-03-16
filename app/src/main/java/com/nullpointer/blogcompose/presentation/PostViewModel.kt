package com.nullpointer.blogcompose.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.request.Disposable
import com.nullpointer.blogcompose.core.states.Resource
import com.nullpointer.blogcompose.core.utils.NetworkException
import com.nullpointer.blogcompose.domain.post.PostRepoImpl
import com.nullpointer.blogcompose.domain.preferences.PreferencesRepoImpl
import com.nullpointer.blogcompose.models.MyPost
import com.nullpointer.blogcompose.models.Post
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import timber.log.Timber
import java.lang.Exception
import java.lang.NullPointerException
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(
    private val postRepo: PostRepoImpl,
) : ViewModel() {

    // * state message to show any error or message
    private val _messagePost = Channel<String>()
    val messagePost = _messagePost.receiveAsFlow()

    // * var to saved the job, to request new data
    // * this for can cancel this work
    private var jobRequestNew: Job? = null
    private val _stateLoadData = MutableStateFlow<Resource<Unit>>(Resource.Loading())
    val stateLoad = _stateLoadData.asStateFlow()

    // * var to save the job, to request post concatenate
    // * this for can cancel this work
    private var jobConcatenatePost: Job? = null
    private val _stateConcatenateData = MutableStateFlow<Resource<Unit>>(Resource.Loading())
    val stateConcatenate = _stateConcatenateData.asStateFlow()

    // * var to save job, to like
    private var jobLike: Job? = null

    val listPost = postRepo.listLastPost.catch {
        Timber.d("Error al obtener los post de la base de datos $it")
        _messagePost.trySend("Error desconocido")
    }.flowOn(Dispatchers.IO).stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        emptyList()
    )

    init {
        // * when init this view model , request new post if is needed
        Timber.e("Se inicio el post view model")
    }


    fun requestNewPost(forceRefresh: Boolean = false) {
        // * this init request for new post, this will from cache or new data server
        // * if no there internet launch exception
        jobRequestNew?.cancel()
        jobRequestNew = viewModelScope.launch(Dispatchers.IO) {
            _stateLoadData.value = Resource.Loading()
            try {
                val sizeNewPost = postRepo.requestLastPost(forceRefresh)
                Timber.d("Se obtuvieron $sizeNewPost post nuevos ")
                _stateLoadData.value = Resource.Success(Unit)
            } catch (e: Exception) {
                _stateLoadData.value = Resource.Failure(e)
                when (e) {
                    is CancellationException -> throw e
                    is NetworkException -> _messagePost.trySend("Verifique su conexion a internet")
                    is NullPointerException -> Timber.e(" Error al obtener ultimas notificaciones El usuario posiblemente es nulo")
                    else -> {
                        _messagePost.trySend("Error desconocido")
                        Timber.e("Error en el request de todos los post $e")
                    }
                }
            }
        }
    }

    fun concatenatePost() {
        // * this init new data but, consideration las post saved,
        // * this for concatenate new post and no override the database
        // * launch exception if no there internet
        jobConcatenatePost?.cancel()
        jobConcatenatePost = viewModelScope.launch(Dispatchers.IO) {
            _stateConcatenateData.value = Resource.Loading()
            try {
                val sizeRequest = postRepo.concatenatePost()
                Timber.d("Datos concatenados $sizeRequest")
                _stateConcatenateData.value = Resource.Success(Unit)
            } catch (e: Exception) {
                _stateConcatenateData.value = Resource.Failure(e)
                when (e) {
                    is CancellationException -> throw e
                    is NetworkException -> _messagePost.trySend("Verifique su conexion a internet")
                    else -> {
                        _messagePost.trySend("Error desconocido")
                        Timber.e("Error en el request de todos los post $e")
                    }
                }
            }
        }
    }

    fun likePost(idPost: String, isLiked: Boolean) {
        // * this init like job, update the database with new data with the new data of
        // * server
        jobLike?.cancel()
        jobLike = viewModelScope.launch(Dispatchers.IO) {
            try {
                postRepo.updateLikePost(idPost, isLiked)
            } catch (e: Exception) {
                when (e) {
                    is CancellationException -> throw e
                    is NetworkException -> _messagePost.send("Necesita conexion para esto")
                    else -> {
                        Timber.e("Erro al dar like $e")
                        _messagePost.send("Error desconocido")
                    }
                }
            }
        }
    }

}