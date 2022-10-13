package com.nullpointer.blogcompose.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nullpointer.blogcompose.core.delegates.SavableProperty
import com.nullpointer.blogcompose.core.states.Resource
import com.nullpointer.blogcompose.core.utils.ExceptionManager.sendMessageErrorToException
import com.nullpointer.blogcompose.core.utils.launchSafeIO
import com.nullpointer.blogcompose.domain.notify.NotifyRepository
import com.nullpointer.blogcompose.models.notify.Notify
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class NotifyViewModel @Inject constructor(
    private val notifyRepository: NotifyRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        private const val CONCATENATE_ENABLE = "KEY_CONCATENATE_ENABLE_NOTIFY"
    }

    private var isConcatEnable by SavableProperty(savedStateHandle, CONCATENATE_ENABLE, false)


    var isConcatNotify by mutableStateOf(false)

    var isRequestNotify by mutableStateOf(false)

    // * message to show about any state
    private val _messageNotify = Channel<String>()
    val messageNotify = _messageNotify.receiveAsFlow()

    // * show notification from database
    val listNotify = notifyRepository.listNotify.transform<List<Notify>, Resource<List<Notify>>> {
        isConcatEnable = true
        Resource.Success(it)
    }.catch { e ->
        sendMessageErrorToException(
            exception = Exception(e),
            message = "Error get notify from database",
            _messageNotify
        )
        emit(Resource.Failure)
    }.flowOn(Dispatchers.IO).stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        Resource.Loading
    )


    init {
        requestLastNotify()
    }

    fun concatenateNotify(callbackSuccess: () -> Unit) = launchSafeIO(
        isEnabled = !isConcatNotify && isConcatEnable,
        blockBefore = { isConcatNotify = true },
        blockAfter = { isConcatNotify = false },
        blockException = {
            sendMessageErrorToException(
                exception = it,
                message = "Error concatenate notify",
                _messageNotify
            )
        },
        blockIO = {
            val countNotify = notifyRepository.concatenateNotify()
            withContext(Dispatchers.Main) {
                if (countNotify == 0) isConcatEnable = false else callbackSuccess()
            }
        }
    )

    fun requestLastNotify(forceRefresh: Boolean = false) = launchSafeIO(
        isEnabled = !isRequestNotify,
        blockBefore = { isRequestNotify = true },
        blockAfter = { isRequestNotify = false },
        blockException = {
            sendMessageErrorToException(
                exception = it,
                message = "Error request last notify",
                _messageNotify
            )
        },
        blockIO = {
            val countNotify = notifyRepository.requestLastNotify(forceRefresh)
            Timber.d("notify get for request :$countNotify")
        }
    )

    fun openNotifications(notify: Notify) = launchSafeIO {
        notifyRepository.openNotify(notify)
    }

}