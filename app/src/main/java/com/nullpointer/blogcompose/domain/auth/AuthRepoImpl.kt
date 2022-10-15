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

    override val myUser: Flow<AuthUser> = prefDataSource.user

    override suspend fun getIdUser() = this.myUser.first().id

    override suspend fun authWithCredential(authCredential: AuthCredential) {
        // * authenticate user and save data user
        val user = callApiTimeOut {
            authDataSource.authWithCredential(authCredential)
        }
        prefDataSource.updateUser(user)
    }

    override suspend fun updateTokenUser(token: String) {
        val oldToken = myUser.first().token
        callApiTimeOut {
            authDataSource.addingTokenUser(
                newToken = token,
                oldToken = oldToken
            )
        }
    }

    override suspend fun logOut() {
        // * log out the user and deleter user saved
        authDataSource.logOut()
        prefDataSource.deleterUser()
    }

    override suspend fun uploadDataUser(urlImg: String?, name: String?) {
        callApiTimeOut {
            val newUser = myUser.first().let {
                if (urlImg != null) it.copy(urlImg = urlImg) else it
            }.let {
                if (name != null) it.copy(name = name) else it
            }
            val updateUser = authDataSource.updateDataUser(newUser)
            prefDataSource.updateUser(updateUser)
        }
    }


    override suspend fun createNewUser(user: AuthUser) {
        callApiTimeOut {
            val newUriImg = imagesDataSource.uploadImageUserWithOutState(user.urlImg.toUri())
            val newUser = user.copy(urlImg = newUriImg.toString())
            val updateUser = authDataSource.createNewUser(newUser)
            prefDataSource.updateUser(updateUser)
        }

    }
}