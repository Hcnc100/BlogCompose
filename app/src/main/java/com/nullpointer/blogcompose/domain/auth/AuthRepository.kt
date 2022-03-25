package com.nullpointer.blogcompose.domain.auth

import com.nullpointer.blogcompose.models.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val user: Flow<User>
    suspend fun authWithTokeGoogle(token: String)
    suspend fun deleterUser()
    suspend fun uploadDataUser(urlImg: String?, name: String?)
    suspend fun logOut()
}