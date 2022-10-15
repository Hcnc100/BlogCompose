package com.nullpointer.blogcompose.data.local.preferences

import com.nullpointer.blogcompose.models.users.AuthUser
import kotlinx.coroutines.flow.Flow

interface PreferencesDataSource {
    val user: Flow<AuthUser>
    suspend fun getCurrentUser(): AuthUser
    suspend fun getIdUser(): String
    suspend fun deleterUser()
    suspend fun updateUser(myUser: AuthUser)
}