package com.nullpointer.blogcompose.domain.auth

import com.nullpointer.blogcompose.data.local.PreferencesDataSource
import com.nullpointer.blogcompose.data.remote.AuthDataSource
import com.nullpointer.blogcompose.models.User
import kotlinx.coroutines.flow.Flow

class AuthRepoImpl(
    private val authDataSource: AuthDataSource,
    private val prefDataSource: PreferencesDataSource,
) : AuthRepository {

    override val user: Flow<User> = prefDataSource.getUserFromProtoStore()

    override suspend fun authWithTokeGoogle(token: String) {
        val user = authDataSource.authWithTokenGoogle(token)
        prefDataSource.saveUser(user)
    }

    override suspend fun deleterUser() {
        authDataSource.deleterUser()
        deleterUser()
    }

    override suspend fun updatePhotoUser(urlImg: String) =
        authDataSource.updateImgUser(urlImg)

    override suspend fun uploadNameUser(name: String) =
        authDataSource.updateNameUser(name)

    override suspend fun uploadDataUser(urlImg: String, name: String) =
        authDataSource.updateDataUser(name, urlImg)

    override suspend fun logOut() {
        authDataSource.logOut()
        deleterUser()
    }


}