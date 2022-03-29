package com.nullpointer.blogcompose.domain.preferences


import com.nullpointer.blogcompose.models.users.MyUser
import kotlinx.coroutines.flow.Flow

interface PreferencesRepository {
    fun getCurrentUser(): Flow<MyUser>
    suspend fun updateUser(myUser: MyUser)
}