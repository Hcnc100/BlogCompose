package com.nullpointer.blogcompose.domain.auth

import com.google.firebase.auth.AuthCredential
import com.nullpointer.blogcompose.core.utils.InternetCheck
import com.nullpointer.blogcompose.core.utils.NetworkException
import com.nullpointer.blogcompose.data.local.PreferencesDataSource
import com.nullpointer.blogcompose.data.remote.AuthDataSource
import com.nullpointer.blogcompose.data.remote.ImagesDataSource
import com.nullpointer.blogcompose.data.remote.ImgProfileInvalid
import com.nullpointer.blogcompose.models.users.MyUser
import kotlinx.coroutines.flow.Flow

class AuthRepoImpl(
    private val authDataSource: AuthDataSource,
    private val prefDataSource: PreferencesDataSource,
    private val imagesDataSource: ImagesDataSource,
) : AuthRepository {

    override val myUser: Flow<MyUser> = prefDataSource.getUserFromProtoStore()

    override suspend fun authWithCredential(authCredential: AuthCredential) {
        val user = authDataSource.authWithCredential(authCredential)
        prefDataSource.saveUser(user)
    }

    override suspend fun updateTokenUser(token: String) {
        authDataSource.updateTokenUser(token)
    }

    override suspend fun deleterUser() {
        authDataSource.deleterUser()
        prefDataSource.deleterUser()
    }

    override suspend fun uploadDataUser(urlImg: String, name: String) {
        if (!InternetCheck.isNetworkAvailable()) throw NetworkException()
        val newUrlImg = urlImg.let {
            if (imagesDataSource.invalidPhotoUser()) throw ImgProfileInvalid()
            imagesDataSource.getImageUser()
        }?.toString()
        val updateUser = authDataSource.updateDataUser(name, newUrlImg)
        prefDataSource.saveUser(updateUser)
    }

    override suspend fun logOut() {
        authDataSource.logOut()
        deleterUser()
    }


}