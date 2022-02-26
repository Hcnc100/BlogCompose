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


@HiltViewModel
class AuthViewModel(
    private val imagesRepoImpl: ImagesRepoImpl,
    private val authRepoImpl: AuthRepoImpl,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    val nameUser = mutableStateOf(authRepoImpl.nameUser)
    val photoUser = mutableStateOf(authRepoImpl.urlImgProfile)
    val isDataComplete = mutableStateOf(authRepoImpl.isDataComplete)

    private val _stateAuth = Channel<LoginStatus>()
    val stateAuth = _stateAuth.receiveAsFlow()

    fun authWithTokeGoogle(token: String) = viewModelScope.launch {
        _stateAuth.send(LoginStatus.Authenticating)
        try {
            authRepoImpl.authWithTokeGoogle(token)
            _stateAuth.send(LoginStatus.Authenticated)
        } catch (e: Exception) {
            _stateAuth.send(LoginStatus.Error(e))
        }
    }
}