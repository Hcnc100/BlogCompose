package com.nullpointer.blogcompose.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nullpointer.blogcompose.core.delegates.SavableProperty
import com.nullpointer.blogcompose.core.states.Resource
import com.nullpointer.blogcompose.core.utils.NetworkException
import com.nullpointer.blogcompose.domain.post.PostRepoImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(
    private val postRepo: PostRepoImpl,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object{
        private const val KEY_CONCATENATE_ENABLE="KEY_CONCATENATE_ENABLE_POST"
    }

    private var isConcatenateEnable by SavableProperty(savedStateHandle,
        KEY_CONCATENATE_ENABLE,true)

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
    private val _stateConcatenateData = MutableStateFlow<Resource<Unit>?>(null)
    val stateConcatenate = _stateConcatenateData.asStateFlow()

    val listPost = postRepo.listLastPost.catch {
        Timber.d("Error al obtener los post de la base de datos $it")
        _messagePost.trySend("Error desconocido")
    }.flowOn(Dispatchers.IO).stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        null
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
        jobRequestNew = viewModelScope.launch(Dispatchers.IO) {
            _stateLoadData.value = Resource.Loading()
            _stateLoadData.value=try {
                val sizeNewPost = postRepo.requestLastPost(forceRefresh)
                Timber.d("Se obtuvieron $sizeNewPost post nuevos ")
                Resource.Success(Unit)
            } catch (e: Exception) {
                when (e) {
                    is CancellationException -> throw e
                    is NetworkException -> _messagePost.trySend("Verifique su conexion a internet")
                    is NullPointerException -> Timber.e(" Error al obtener ultimas notificaciones El usuario posiblemente es nulo")
                    else -> {
                        _messagePost.trySend("Error desconocido")
                        Timber.e("Error en el request de todos los post $e")
                    }
                }
                Resource.Failure(e)
            }
        }
    }

    fun concatenatePost() {
        // * this init new data but, consideration las post saved,
        // * this for concatenate new post and no override the database
        // * launch exception if no there internet
        if (isConcatenateEnable){
            jobConcatenatePost?.cancel()
            jobConcatenatePost = viewModelScope.launch(Dispatchers.IO) {
                _stateConcatenateData.value = Resource.Loading()
                try {
                    postRepo.concatenatePost().let {
                        Timber.d("Post concatenados $it")
                        if(it==0) isConcatenateEnable=false
                    }
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

    }
}