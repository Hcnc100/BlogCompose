package com.nullpointer.blogcompose.domain.auth

import com.google.firebase.auth.AuthCredential
import com.nullpointer.blogcompose.models.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val user: Flow<User>
    suspend fun authWithCredential(authCredential: AuthCredential)
    suspend fun updateTokenUser(token:String)
    suspend fun deleterUser()
    suspend fun uploadDataUser(urlImg: String?, name: String?)
    suspend fun logOut()
}