package com.nullpointer.blogcompose.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.AuthCredential
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
) : ViewModel() {

    init {
        Timber.e("Hola este es el auth view model")
    }

    val currentUser = authRepoImpl.user.flowOn(Dispatchers.IO).stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        null
    )

    private val _messageAuth = Channel<String>()
    val messageAuth = _messageAuth.receiveAsFlow()

    val isDataComplete: Boolean
        get() = stateAuthUser.value is LoginStatus.Authenticated.CompleteData

    val stateAuthUser = flow {
        authRepoImpl.user.collect { user ->
            val state = if (user.uuid.isEmpty()) {
                LoginStatus.Unauthenticated
            } else {
                if (user.nameUser.isEmpty() || user.uuid.isEmpty()) {
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


    private val _stateAuthentication = MutableStateFlow<Resource<Unit>?>(null)
    val stateAuthentication = _stateAuthentication.asStateFlow()

    fun authWithCredential(authCredential: AuthCredential) = viewModelScope.launch {
        _stateAuthentication.value = Resource.Loading()
        try {
            authRepoImpl.authWithCredential(authCredential)
            _stateAuthentication.value = Resource.Success(Unit)
        } catch (e: Exception) {
            when (e) {
                is CancellationException -> throw e
                else -> {
                    _stateAuthentication.value = Resource.Failure(e)
                    Timber.e("Error al auth $e")
                    _messageAuth.trySend("Error al autenticar")
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