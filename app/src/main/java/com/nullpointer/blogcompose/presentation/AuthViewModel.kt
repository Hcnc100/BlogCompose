package com.nullpointer.blogcompose.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.AuthCredential
import com.nullpointer.blogcompose.R
import com.nullpointer.blogcompose.core.states.LoginStatus
import com.nullpointer.blogcompose.core.utils.launchSafeIO
import com.nullpointer.blogcompose.domain.auth.AuthRepository
import com.nullpointer.blogcompose.domain.delete.DeleterRepository
import com.nullpointer.blogcompose.models.users.SimpleUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import timber.log.Timber
import javax.inject.Inject


@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val deleterRepository: DeleterRepository
) : ViewModel() {

    private val SimpleUser.isUserAuth get() = idUser.isNotEmpty()
    private val SimpleUser.isDataComplete get() = name.isNotEmpty() && urlImg.isNotEmpty()

    val currentUser = authRepository.myUser.flowOn(Dispatchers.IO).stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        SimpleUser()
    )

    private val _messageAuth = Channel<Int>()
    val messageAuth = _messageAuth.receiveAsFlow()

    val stateAuthUser = authRepository.myUser.transform { user ->
        val stateUser = when {
            !user.isUserAuth -> LoginStatus.Unauthenticated
            user.isDataComplete -> LoginStatus.Authenticated.CompleteData
            else -> LoginStatus.Authenticated.CompletingData
        }
        emit(stateUser)
    }.catch {
        emit(LoginStatus.Unauthenticated)
        Timber.e("Error get user for preferences $it")
    }.flowOn(
        Dispatchers.IO
    ).stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        LoginStatus.Authenticating
    )


    var isProcessing by mutableStateOf(false)
        private set

    fun authWithCredential(
        authCredential: AuthCredential
    ) = launchSafeIO(
        blockBefore = { isProcessing = true },
        blockAfter = { isProcessing = false },
        blockException = {
            Timber.e("Error al auth $it")
            _messageAuth.trySend(R.string.message_error_auth)
        },
        blockIO = { authRepository.authWithCredential(authCredential) }
    )

    fun logOut() = launchSafeIO {
        authRepository.logOut()
        deleterRepository.clearAllData()
    }


    fun createNewUser(
        myUser: SimpleUser
    ) = launchSafeIO(
        blockBefore = { isProcessing = true },
        blockAfter = { isProcessing = false },
        blockException = {
            Timber.e("error to create new user $it")
            _messageAuth.trySend(R.string.message_error_login)
        },
        blockIO = { authRepository.createNewUser(myUser) }
    )



}