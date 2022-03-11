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

    // * job to save coroutine concatenate new post
    // * this for update the ui
    private var jobConcatenateNotify: Job? = null
    private val _stateConcatenateData = MutableStateFlow<Resource<Unit>>(Resource.Loading())
    val stateConcatenate = _stateConcatenateData.asStateFlow()

    // * job to save coroutine request last post
    // * this for update ui
    private var jobRequestNotify: Job? = null
    private val _stateRequestData = MutableStateFlow<Resource<Unit>>(Resource.Loading())
    val stateRequest = _stateRequestData.asStateFlow()

    // * message to show about any state
    private val _messageNotify = Channel<String>()
    val messageNotify = _messageNotify.receiveAsFlow()

    // * show notification from database
    val listNotify: Flow<List<Notify>> = notifyRepoImpl.listNotify.catch { e ->
        Timber.e("Error al obtener las notificaciones de la base de datos $e")
        _messageNotify.trySend("Error desconocido")
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        emptyList()
    )


    init {
        // * always request last notifications
        requestLastNotify()
    }

    fun concatenateNotify() {
        // * request notifications and add to databse
        // * stop job if is alive and create new request
        jobConcatenateNotify?.cancel()
        jobConcatenateNotify = viewModelScope.launch(Dispatchers.IO) {
            _stateConcatenateData.value = Resource.Loading()
            try {
                notifyRepoImpl.concatenateNotify().let {
                    Timber.d("numero de notificaciones obtenidas CONCATENATE:$it")
                }
                _stateConcatenateData.value = Resource.Success(Unit)
            } catch (e: Exception) {
                _stateConcatenateData.value = Resource.Failure(e)
                when (e) {
                    is CancellationException -> throw e
                    is NetworkException -> _messageNotify.trySend("Verifique su conexion a internet")
                    else -> {
                        _messageNotify.trySend("Error desconocido")
                        Timber.e("Error en el concatenate $e")
                    }
                }
            }
        }
    }

    fun requestLastNotify(forceRefresh: Boolean = false) {
        // * request las notification, consideration the first notification
        // * order by time in the database, or also force refresh data
        jobRequestNotify?.cancel()
        jobRequestNotify = viewModelScope.launch {
            _stateRequestData.value = Resource.Loading()
            try {
                notifyRepoImpl.requestLastNotify(forceRefresh).let {
                    Timber.d("numero de notificaciones obtenidas REQUEST:$it")
                }
                _stateRequestData.value = Resource.Success(Unit)
            } catch (e: Exception) {
                _stateRequestData.value = Resource.Failure(e)
                when (e) {
                    is CancellationException -> throw e
                    is NetworkException -> _messageNotify.trySend("Verifique su conexion a internet")
                    else -> {
                        _messageNotify.trySend("Error desconocido")
                        Timber.e("Error al obtener ultimas notificaciones $e")
                    }
                }
            }
        }
    }

}