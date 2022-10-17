package com.nullpointer.blogcompose.domain.auth

import com.google.firebase.auth.AuthCredential
import com.nullpointer.blogcompose.models.users.AuthUser
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val myUser: Flow<AuthUser>
    suspend fun logOut()
    suspend fun verifyTokenUser()
    suspend fun createNewUser(user: AuthUser)
    suspend fun updateTokenUser(token: String)
    suspend fun authWithCredential(authCredential: AuthCredential)
    suspend fun uploadDataUser(urlImg: String? = null, name: String? = null)
}