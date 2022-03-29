package com.nullpointer.blogcompose.domain.preferences

import com.nullpointer.blogcompose.data.local.PreferencesDataSource
import com.nullpointer.blogcompose.models.users.MyUser
import kotlinx.coroutines.flow.Flow

class PreferencesRepoImpl(
    private val preferencesDataSource: PreferencesDataSource,
) : PreferencesRepository {


    override fun getCurrentUser(): Flow<MyUser> =
        preferencesDataSource.getUserFromProtoStore()

    override suspend fun updateUser(myUser: MyUser) =
        preferencesDataSource.saveUser(myUser)

}