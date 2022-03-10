package com.nullpointer.blogcompose.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nullpointer.blogcompose.core.states.Resource
import com.nullpointer.blogcompose.core.utils.NetworkException
import com.nullpointer.blogcompose.domain.notify.NotifyRepoImpl
import com.nullpointer.blogcompose.models.Notify
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import java.lang.Exception
import java.util.concurrent.CancellationException
import javax.inject.Inject

@HiltViewModel
class NotifyViewModel @Inject constructor(
    private val notifyRepoImpl: NotifyRepoImpl,
) : ViewModel() {

    private var jobConcatenateNotify: Job? = null
    private val _stateConcatenateData = MutableStateFlow<Resource<Unit>>(Resource.Loading())
    val stateConcatenate = _stateConcatenateData.asStateFlow()

    private var jobRequestNotify: Job? = null
    private val _stateRequestData = MutableStateFlow<Resource<Unit>>(Resource.Loading())
    val stateRequest= _stateRequestData.asStateFlow()

    private val _messageNotify = Channel<String>()
    val messagePost = _messageNotify.receiveAsFlow()

    val listNotify = flow<Resource<List<Notify>>> {
        notifyRepoImpl.getAllNotifications().collect {
            emit(Resource.Success(it))
        }
    }.catch { e ->
        Timber.e("Error al obtener las notificaciones de la base de datos $e")
        emit(Resource.Failure(Exception(e)))
    }.stateIn(viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        Resource.Loading()
    )

    fun concatenateNotify(){
        jobConcatenateNotify?.cancel()
        jobConcatenateNotify=viewModelScope.launch(Dispatchers.IO) {
            _stateConcatenateData.value = Resource.Loading()
            try{
                notifyRepoImpl.requestLastNotify()
                _stateConcatenateData.value=Resource.Success(Unit)
            }catch (e:Exception){
                _stateConcatenateData.value = Resource.Failure(e)
                when (e) {
                    is CancellationException -> throw e
                    is NetworkException -> _messageNotify.trySend("Verifique su conexion a internet")
                    else -> {
                        _messageNotify.trySend("Error desconocido")
                        Timber.d("Error en el concatenate $e")
                    }
                }
            }
        }
    }

    fun requestLastNotify(){
        jobRequestNotify?.cancel()
        jobRequestNotify=viewModelScope.launch {
            _stateRequestData.value=Resource.Loading()
            try {
                notifyRepoImpl.requestLastNotify()
                _stateRequestData.value=Resource.Success(Unit)
            }catch (e:Exception){
                _stateRequestData.value = Resource.Failure(e)
                when (e) {
                    is CancellationException -> throw e
                    is NetworkException -> _messageNotify.trySend("Verifique su conexion a internet")
                    else -> {
                        _messageNotify.trySend("Error desconocido")
                        Timber.d("Error en el request $e")
                    }
                }
            }
        }
    }

}