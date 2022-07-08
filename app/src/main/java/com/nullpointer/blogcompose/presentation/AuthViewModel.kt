package com.nullpointer.blogcompose.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.AuthCredential
import com.nullpointer.blogcompose.R
import com.nullpointer.blogcompose.core.delegates.SavableComposeState
import com.nullpointer.blogcompose.core.states.LoginStatus
import com.nullpointer.blogcompose.core.states.Resource
import com.nullpointer.blogcompose.domain.auth.AuthRepoImpl
import com.nullpointer.blogcompose.domain.notify.NotifyRepoImpl
import com.nullpointer.blogcompose.domain.post.PostRepoImpl
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
            val state = if (user.idUser.isEmpty()) {
                LoginStatus.Unauthenticated
            } else {
                if (user.nameUser.isEmpty() || user.idUser.isEmpty() || user.urlImg.isEmpty()) {
                    LoginStatus.Authenticated.CompletingData
                } else {
                    LoginStatus.Authenticated.CompleteData
                }
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


    var isLoading by SavableComposeState(savedStateHandle,"",false)
    private set

    fun authWithCredential(
        authCredential: AuthCredential
    ) = viewModelScope.launch(Dispatchers.IO) {
        try {
            isLoading=true
            authRepoImpl.authWithCredential(authCredential)
        } catch (e: Exception) {
            when (e) {
                is CancellationException -> throw e
                else -> {
                    Timber.e("Error al auth $e")
                    _messageAuth.trySend(R.string.message_error_auth)
                }
            }
        }
    }

    fun logOut() = viewModelScope.launch {
        authRepoImpl.logOut()
        notifyRepoImpl.deleterAllNotify()
        postRepoImpl.deleterAllPost()
    }
}