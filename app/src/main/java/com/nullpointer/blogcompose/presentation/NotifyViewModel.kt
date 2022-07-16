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
import com.nullpointer.blogcompose.domain.notify.NotifyRepository
import com.nullpointer.blogcompose.models.notify.Notify
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.concurrent.CancellationException
import javax.inject.Inject

@HiltViewModel
class NotifyViewModel @Inject constructor(
    private val notifyRepository: NotifyRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        private const val KEY_CONCATENATE_ENABLE = "KEY_CONCATENATE_ENABLE_NOTIFY"
    }

    private var isConcatEnable by SavableProperty(
        savedStateHandle,
        KEY_CONCATENATE_ENABLE, true
    )

    // * job to save coroutine concatenate new post
    // * this for update the ui
    private var jobConcatNotify: Job? = null
    var stateConcatNotify by mutableStateOf(false)

    // * job to save coroutine request last post
    // * this for update ui
    private var jobRequestNotify: Job? = null
    var stateRequestNotify by mutableStateOf(false)

    // * message to show about any state
    private val _messageNotify = Channel<Int>()
    val messageNotify = _messageNotify.receiveAsFlow()

    // * show notification from database
    val listNotify = flow<Resource<List<Notify>>> {
        notifyRepository.listNotify.collect {
            emit(Resource.Success(it))
        }
    }.catch { e ->
        Timber.e("Error get notify from database $e")
        _messageNotify.trySend(R.string.message_error_unknown)
        emit(Resource.Failure)
    }.flowOn(Dispatchers.IO).stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        Resource.Loading
    )


    init {
        requestLastNotify()
    }

    fun concatenateNotify(callbackSuccess: () -> Unit){
        // * request notifications and add to databse
        // * stop job if is alive and create new request
        if (isConcatEnable) {
            jobConcatNotify?.cancel()
            jobConcatNotify = viewModelScope.launch {
                Timber.d("Init process concatenate notify")
                stateConcatNotify = true
                try {
                    val countNotify =
                        withContext(Dispatchers.IO) { notifyRepository.concatenateNotify() }
                    Timber.d("notify get with concatenate $countNotify")
                    if (countNotify == 0) isConcatEnable = false else callbackSuccess()
                } catch (e: Exception) {
                    when (e) {
                        is CancellationException -> throw e
                        is NetworkException -> _messageNotify.trySend(R.string.message_error_internet_checker)
                        else -> {
                            _messageNotify.trySend(R.string.message_error_unknown)
                            Timber.e("Error en el concatenate notify $e")
                        }
                    }
                } finally {
                    stateConcatNotify = false
                }
            }
        }

    }

    fun requestLastNotify(forceRefresh: Boolean = false) {
        // * request las notification, consideration the first notification
        // * order by time in the database, or also force refresh data
        jobRequestNotify?.cancel()
        jobRequestNotify = viewModelScope.launch {
            stateRequestNotify = true
            try {
                val countNotify =
                    withContext(Dispatchers.IO) { notifyRepository.requestLastNotify(forceRefresh) }
                isConcatEnable = true
                Timber.d("notify get for request :$countNotify")
            } catch (e: Exception) {
                when (e) {
                    is CancellationException -> throw e
                    is NetworkException -> _messageNotify.trySend(R.string.message_error_internet_checker)
                    is NullPointerException -> Timber.e(" Error al obtener ultimas notificaciones El usuario posiblemente es nulo")
                    else -> {
                        _messageNotify.trySend(R.string.message_error_unknown)
                        Timber.e("Error al obtener ultimas notificaciones $e")
                    }
                }
            } finally {
                stateRequestNotify = false
            }
        }
    }

    fun openNotifications(
        notify: Notify
    ) = viewModelScope.launch(Dispatchers.IO) {
        notifyRepository.openNotify(notify)
    }

}