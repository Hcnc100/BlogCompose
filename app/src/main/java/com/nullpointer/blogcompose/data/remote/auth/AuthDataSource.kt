package com.nullpointer.blogcompose.data.remote.auth

import com.google.firebase.auth.AuthCredential
import com.nullpointer.blogcompose.models.users.MyUser

interface AuthDataSource {
    suspend fun authWithCredential(credential: AuthCredential): MyUser
    suspend fun updateDataUser(name: String?, urlImg: String?): MyUser
    suspend fun updateFullDataUser(name: String, urlImg: String): MyUser
    suspend fun updateTokenUser(token: String? = null, uuidUser: String? = null)
    suspend fun deleterUser()
    fun logOut()
}