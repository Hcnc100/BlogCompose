package com.nullpointer.blogcompose.data.local.preferences

import com.nullpointer.blogcompose.models.users.AuthUser
import kotlinx.coroutines.flow.Flow

interface PreferencesDataSource {
    suspend fun deleterData()
    fun getUser(): Flow<AuthUser>
    suspend fun updateUser(user: AuthUser)
    suspend fun updateUser(name: String? = null, urlImg: String? = null, token: String? = null)
}