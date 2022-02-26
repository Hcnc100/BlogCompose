package com.nullpointer.blogcompose.core.states

import java.lang.Exception

sealed class LoginStatus {
    object Unauthenticated:LoginStatus()
    object Authenticated : LoginStatus()
    object Authenticating : LoginStatus()
    data class Error(val exception: Exception) : LoginStatus()
}
