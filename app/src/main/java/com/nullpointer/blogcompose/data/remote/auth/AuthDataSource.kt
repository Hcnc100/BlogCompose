package com.nullpointer.blogcompose.data.remote.auth

import com.google.firebase.auth.AuthCredential
import com.nullpointer.blogcompose.models.users.AuthUser

interface AuthDataSource {
    suspend fun authWithCredential(credential: AuthCredential): AuthUser
    suspend fun updateDataUser(name: String?, urlImg: String?)
    suspend fun addingTokenUser(
        newToken: String? = null,
        uuidUser: String? = null,
        oldToken: String = ""
    )

    suspend fun createNewUser(user: AuthUser): AuthUser
    suspend fun deleterUser()
    suspend fun logOut()
    suspend fun getUserToken(): String
}