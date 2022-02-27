package com.nullpointer.blogcompose.domain.auth

import com.nullpointer.blogcompose.data.remote.AuthDataSource

class AuthRepoImpl(
    private val authDataSource: AuthDataSource,
) : AuthRepository {
    override val isDataComplete = authDataSource.isDataComplete
    override val urlImgProfile = authDataSource.uriImgUser
    override val nameUser: String? = authDataSource.nameUser
    override val uuidUser: String? = authDataSource.uuidUser

    override suspend fun authWithTokeGoogle(token: String) =
        authDataSource.authWithTokeGoogle(token)

    override suspend fun deleterUser() =
        authDataSource.deleterUser()

    override suspend fun updatePhotoUser(urlImg: String) =
        authDataSource.updateImgUser(urlImg)

    override suspend fun uploadNameUser(name: String) =
        authDataSource.updateNameUser(name)

    override suspend fun uploadDataUser(urlImg: String, name: String) =
        authDataSource.updateDataUser(urlImg, name)

    override fun logOut() =
        authDataSource.logOut()


}