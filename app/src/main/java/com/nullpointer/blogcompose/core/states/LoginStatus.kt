package com.nullpointer.blogcompose.core.states

sealed class LoginStatus {
    object Unauthenticated : LoginStatus()
    object Authenticating : LoginStatus()
    sealed class Authenticated : LoginStatus() {
        object CompleteData : LoginStatus()
        object CompletingData : LoginStatus()
    }
}
