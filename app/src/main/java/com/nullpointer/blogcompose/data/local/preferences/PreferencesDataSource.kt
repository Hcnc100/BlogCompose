package com.nullpointer.blogcompose.data.local.preferences

import com.nullpointer.blogcompose.models.users.MyUser
import kotlinx.coroutines.flow.Flow

interface PreferencesDataSource {
    val user: Flow<MyUser>
    suspend fun getCurrentUser(): MyUser
    suspend fun getIdUser(): String
    suspend fun deleterUser()
    suspend fun updateUser(myUser: MyUser)
}