package com.nullpointer.blogcompose.domain.auth

import com.google.firebase.auth.AuthCredential
import com.nullpointer.blogcompose.models.users.SimpleUser
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val myUser: Flow<SimpleUser>
    suspend fun authWithCredential(authCredential: AuthCredential)
    suspend fun updateTokenUser(token: String)
    suspend fun uploadDataUser(urlImg: String? = null, name: String? = null)
    suspend fun logOut()
    suspend fun createNewUser(user: SimpleUser)
    suspend fun getIdUser(): String
}