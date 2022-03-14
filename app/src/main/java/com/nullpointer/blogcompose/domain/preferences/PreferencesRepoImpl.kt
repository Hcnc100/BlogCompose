package com.nullpointer.blogcompose.domain.preferences

import com.nullpointer.blogcompose.data.local.PreferencesDataSource
import com.nullpointer.blogcompose.models.User
import kotlinx.coroutines.flow.Flow

class PreferencesRepoImpl(
    private val preferencesDataSource: PreferencesDataSource,
) : PreferencesRepository {


    override fun getCurrentUser(): Flow<User> =
        preferencesDataSource.getUserFromProtoStore()

    override suspend fun updateUser(user: User) =
        preferencesDataSource.saveUser(user)

}