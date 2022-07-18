package com.nullpointer.blogcompose.data.local.preferences

import com.nullpointer.blogcompose.models.users.SimpleUser
import kotlinx.coroutines.flow.Flow

interface PreferencesDataSource {
    val user: Flow<SimpleUser>
    suspend fun getCurrentUser(): SimpleUser
    suspend fun getIdUser(): String
    suspend fun deleterUser()
    suspend fun updateUser(myUser: SimpleUser)
}