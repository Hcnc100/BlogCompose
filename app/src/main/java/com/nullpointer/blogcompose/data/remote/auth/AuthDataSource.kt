package com.nullpointer.blogcompose.data.remote.auth

import com.google.firebase.auth.AuthCredential
import com.nullpointer.blogcompose.models.users.SimpleUser

interface AuthDataSource {
    suspend fun authWithCredential(credential: AuthCredential): SimpleUser
    suspend fun updateDataUser(name: String?, urlImg: String?): SimpleUser
    suspend fun updateFullDataUser(name: String, urlImg: String): SimpleUser
    suspend fun updateTokenUser(token: String? = null, uuidUser: String? = null)
    suspend fun deleterUser()
    fun logOut()
}