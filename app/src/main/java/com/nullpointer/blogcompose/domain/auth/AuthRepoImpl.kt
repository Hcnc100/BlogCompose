package com.nullpointer.blogcompose.domain.auth

import androidx.core.net.toUri
import com.google.firebase.auth.AuthCredential
import com.nullpointer.blogcompose.core.utils.InternetCheck
import com.nullpointer.blogcompose.core.utils.NetworkException
import com.nullpointer.blogcompose.data.local.preferences.PreferencesDataSource
import com.nullpointer.blogcompose.data.remote.auth.AuthDataSource
import com.nullpointer.blogcompose.data.remote.image.ImagesDataSource
import com.nullpointer.blogcompose.models.users.MyUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class AuthRepoImpl(
    private val authDataSource: AuthDataSource,
    private val prefDataSource: PreferencesDataSource,
    private val imagesDataSource: ImagesDataSource
) : AuthRepository {

    override val myUser: Flow<MyUser> = prefDataSource.user

    override suspend fun getIdUser() = this.myUser.first().idUser

    override suspend fun authWithCredential(authCredential: AuthCredential) {
        // * authenticate user and save data user
        val user = authDataSource.authWithCredential(authCredential)
        prefDataSource.updateUser(user)
    }

    override suspend fun updateTokenUser(token: String) =
        authDataSource.updateTokenUser(token, null)

    override suspend fun logOut() {
        // * log out the user and deleter user saved
        authDataSource.logOut()
        prefDataSource.deleterUser()
    }

    override suspend fun uploadDataUser(urlImg: String?, name: String?) {
        if (!InternetCheck.isNetworkAvailable()) throw NetworkException()
        val updateUser = authDataSource.updateDataUser(name, urlImg)
        prefDataSource.updateUser(updateUser)
    }


    override suspend fun createNewUser(user: MyUser) {
        if (!InternetCheck.isNetworkAvailable()) throw NetworkException()
        val uriImg = imagesDataSource.uploadImageUserWithState(user.urlImg.toUri())
        val updateUser = authDataSource.updateFullDataUser(user.name, uriImg.toString())
        prefDataSource.updateUser(updateUser)
    }




}