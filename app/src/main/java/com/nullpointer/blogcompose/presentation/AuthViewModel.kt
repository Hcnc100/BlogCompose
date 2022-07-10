package com.nullpointer.blogcompose.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.AuthCredential
import com.nullpointer.blogcompose.R
import com.nullpointer.blogcompose.core.delegates.SavableComposeState
import com.nullpointer.blogcompose.core.states.LoginStatus
import com.nullpointer.blogcompose.domain.auth.AuthRepoImpl
import com.nullpointer.blogcompose.domain.notify.NotifyRepoImpl
import com.nullpointer.blogcompose.domain.post.PostRepoImpl
import com.nullpointer.blogcompose.models.users.MyUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepoImpl: AuthRepoImpl,
    private val notifyRepoImpl: NotifyRepoImpl,
    private val postRepoImpl: PostRepoImpl,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    init {
        Timber.e("Hola este es el auth view model")
    }

    val currentUser = authRepoImpl.myUser.flowOn(Dispatchers.IO).stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        null
    )

    private val _messageAuth = Channel<Int>()
    val messageAuth = _messageAuth.receiveAsFlow()

    val isDataComplete: Boolean
        get() = stateAuthUser.value is LoginStatus.Authenticated.CompleteData

    val stateAuthUser = flow {
        authRepoImpl.myUser.collect { user ->
            val state = if (!user.isUserAuth) {
                LoginStatus.Unauthenticated
            } else if (user.isDataComplete) {
                LoginStatus.Authenticated.CompleteData
            } else {
                LoginStatus.Authenticated.CompletingData
            }
            emit(state)
        }
    }.catch {
        emit(LoginStatus.Unauthenticated)
        Timber.e("Error al obtener el usuario del las preferencias $it")
    }.flowOn(
        Dispatchers.IO
    ).stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        LoginStatus.Authenticating
    )


    var isLoading by SavableComposeState(savedStateHandle, "", false)
        private set

    fun authWithCredential(
        authCredential: AuthCredential
    ) = viewModelScope.launch(Dispatchers.IO) {
        try {
            isLoading = true
            authRepoImpl.authWithCredential(authCredential)
        } catch (e: Exception) {
            when (e) {
                is CancellationException -> throw e
                else -> {
                    Timber.e("Error al auth $e")
                    _messageAuth.trySend(R.string.message_error_auth)
                }
            }
        } finally {
            isLoading = false
        }
    }

    fun logOut() = viewModelScope.launch {
        authRepoImpl.logOut()
        notifyRepoImpl.deleterAllNotify()
        postRepoImpl.deleterAllPost()
    }

    fun updateUser(user: MyUser) = viewModelScope.launch(Dispatchers.IO) {
        authRepoImpl.uploadDataUser(user.name, name = user.name)
    }

     var creatingUser by mutableStateOf(false)
        private set

    fun createNewUser(myUser: MyUser) = viewModelScope.launch(Dispatchers.IO) {
        try {
            creatingUser = true
            authRepoImpl.createNewUser(myUser)
        } catch (e: Exception) {

        } finally {
            creatingUser = false
        }
    }
}