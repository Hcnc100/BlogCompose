package com.nullpointer.blogcompose.domain.auth

import androidx.core.net.toUri
import com.google.firebase.auth.AuthCredential
import com.nullpointer.blogcompose.core.utils.callApiTimeOut
import com.nullpointer.blogcompose.data.local.preferences.PreferencesDataSource
import com.nullpointer.blogcompose.data.remote.auth.AuthDataSource
import com.nullpointer.blogcompose.data.remote.image.ImagesDataSource
import com.nullpointer.blogcompose.models.users.AuthUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class AuthRepoImpl(
    private val authDataSource: AuthDataSource,
    private val prefDataSource: PreferencesDataSource,
    private val imagesDataSource: ImagesDataSource
) : AuthRepository {

    override val myUser: Flow<AuthUser> = prefDataSource.getUser()

    override suspend fun verifyTokenUser() {
        val lastTokenSaved = prefDataSource.getUser().first().token
        val newTokenSaved = authDataSource.getUserToken()

        if (lastTokenSaved != newTokenSaved) {
            callApiTimeOut { authDataSource.addingTokenUser(newToken = newTokenSaved) }
            prefDataSource.updateUser(token = newTokenSaved)
        }
    }

    override suspend fun authWithCredential(authCredential: AuthCredential) {
        // * authenticate user and save data user
        val user = callApiTimeOut { authDataSource.authWithCredential(authCredential) }
        prefDataSource.updateUser(user)
    }

    override suspend fun updateTokenUser(token: String) {
        val oldToken = prefDataSource.getUser().first().token
        callApiTimeOut {
            authDataSource.addingTokenUser(
                newToken = token,
                oldToken = oldToken
            )
        }
        prefDataSource.updateUser(token = token)
    }

    override suspend fun logOut() {
        // * log out the user and deleter user saved
        authDataSource.logOut()
        prefDataSource.deleterData()
    }

    override suspend fun uploadDataUser(urlImg: String?, name: String?) {
        callApiTimeOut { authDataSource.updateDataUser(name, urlImg) }
        prefDataSource.updateUser(name, urlImg)
    }


    override suspend fun createNewUser(user: AuthUser) {
        val updateUser = callApiTimeOut(10_000) {
            val newUriImg = imagesDataSource.uploadImageUserWithOutState(user.urlImg.toUri())
            val newUser = user.copy(urlImg = newUriImg.toString())
            authDataSource.createNewUser(newUser)
        }
        prefDataSource.updateUser(updateUser)
    }
}