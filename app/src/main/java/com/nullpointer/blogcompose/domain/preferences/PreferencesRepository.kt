package com.nullpointer.blogcompose.domain.preferences

import com.nullpointer.blogcompose.models.User
import kotlinx.coroutines.flow.Flow

interface PreferencesRepository {
    fun getCurrentUser(): Flow<User>
    suspend fun updateUser(user: User)
}