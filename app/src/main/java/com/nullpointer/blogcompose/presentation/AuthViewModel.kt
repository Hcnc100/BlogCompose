package com.nullpointer.blogcompose.presentation

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nullpointer.blogcompose.core.states.LoginStatus
import com.nullpointer.blogcompose.domain.auth.AuthRepoImpl
import com.nullpointer.blogcompose.domain.images.ImagesRepoImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow

import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AuthViewModel @Inject constructor(
    private val imagesRepoImpl: ImagesRepoImpl,
    private val authRepoImpl: AuthRepoImpl,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    val nameUser = mutableStateOf(authRepoImpl.nameUser)
    val photoUser = mutableStateOf(authRepoImpl.urlImgProfile)
    val isDataComplete = mutableStateOf(authRepoImpl.isDataComplete)
    private val _messageErrorAuth = Channel<String>()
    val messageErrorAuth = _messageErrorAuth.receiveAsFlow()

    private val _stateAuth = MutableStateFlow(
        if (authRepoImpl.uuidUser.isNullOrBlank())
            LoginStatus.Unauthenticated else LoginStatus.Authenticated
    )
     val stateAuth = _stateAuth.asStateFlow()


    fun authWithTokeGoogle(token: String) = viewModelScope.launch {
        _stateAuth.value = LoginStatus.Authenticating
        try {
            authRepoImpl.authWithTokeGoogle(token)
            _stateAuth.value = LoginStatus.Authenticated
        } catch (e: Exception) {
            _messageErrorAuth.send("Error $e")
            _stateAuth.value = LoginStatus.Unauthenticated
        }
    }

    fun logOut() {
        _stateAuth.value=LoginStatus.Unauthenticated
        authRepoImpl.logOut()
    }
}