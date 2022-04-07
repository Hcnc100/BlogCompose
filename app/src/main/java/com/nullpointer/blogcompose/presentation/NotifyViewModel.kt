package com.nullpointer.blogcompose.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nullpointer.blogcompose.R
import com.nullpointer.blogcompose.core.delegates.SavableProperty
import com.nullpointer.blogcompose.core.states.Resource
import com.nullpointer.blogcompose.core.utils.NetworkException
import com.nullpointer.blogcompose.domain.notify.NotifyRepoImpl
import com.nullpointer.blogcompose.models.notify.Notify
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.CancellationException
import javax.inject.Inject

@HiltViewModel
class NotifyViewModel @Inject constructor(
    private val notifyRepoImpl: NotifyRepoImpl,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object{
        private const val KEY_CONCATENATE_ENABLE="KEY_CONCATENATE_ENABLE_NOTIFY"
    }

    private var isConcatenateEnable by SavableProperty(savedStateHandle,
        KEY_CONCATENATE_ENABLE,true)

    // * job to save coroutine concatenate new post
    // * this for update the ui
    private var jobConcatenateNotify: Job? = null
    private val _stateConcatenateData = MutableStateFlow<Resource<Unit>?>(null)
    val stateConcatenate = _stateConcatenateData.asStateFlow()

    // * job to save coroutine request last post
    // * this for update ui
    private var jobRequestNotify: Job? = null
    private val _stateRequestData = MutableStateFlow<Resource<Unit>>(Resource.Loading())
    val stateRequest = _stateRequestData.asStateFlow()

    // * message to show about any state
    private val _messageNotify = Channel<Int>()
    val messageNotify = _messageNotify.receiveAsFlow()

    // * show notification from database
    val listNotify = notifyRepoImpl.listNotify.catch { e ->
        Timber.e("Error al obtener las notificaciones de la base de datos $e")
        _messageNotify.trySend(R.string.message_error_unknown)
    }.flowOn(Dispatchers.IO).stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        null
    )


    init {
        // * always request last notifications
        Timber.e("Se inicio el notofy view model")
        requestLastNotify()
    }

    fun concatenateNotify() {
        // * request notifications and add to databse
        // * stop job if is alive and create new request
        if(isConcatenateEnable){
            jobConcatenateNotify?.cancel()
            jobConcatenateNotify = viewModelScope.launch(Dispatchers.IO) {
                _stateConcatenateData.value = Resource.Loading()
                try {
                    notifyRepoImpl.concatenateNotify().let {
                        Timber.d("numero de notificaciones obtenidas CONCATENATE:$it")
                        if(it==0) isConcatenateEnable=false
                    }
                    _stateConcatenateData.value = Resource.Success(Unit)
                } catch (e: Exception) {
                    _stateConcatenateData.value = Resource.Failure(e)
                    when (e) {
                        is CancellationException -> throw e
                        is NetworkException -> _messageNotify.trySend(R.string.message_error_internet_checker)
                        else -> {
                            _messageNotify.trySend(R.string.message_error_unknown)
                            Timber.e("Error en el concatenate $e")
                        }
                    }
                }
            }
        }

    }

    fun requestLastNotify(forceRefresh: Boolean = false) {
        // * request las notification, consideration the first notification
        // * order by time in the database, or also force refresh data
        jobRequestNotify?.cancel()
        jobRequestNotify = viewModelScope.launch(Dispatchers.IO) {
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
                    is NetworkException -> _messageNotify.trySend(R.string.message_error_internet_checker)
                    is NullPointerException -> Timber.e(" Error al obtener ultimas notificaciones El usuario posiblemente es nulo")
                    else -> {
                        _messageNotify.trySend(R.string.message_error_unknown)
                        Timber.e("Error al obtener ultimas notificaciones $e")
                    }
                }
            }
        }
    }

    fun openNotifications(notify: Notify)=viewModelScope.launch{
        notifyRepoImpl.openNotify(notify)
    }

}