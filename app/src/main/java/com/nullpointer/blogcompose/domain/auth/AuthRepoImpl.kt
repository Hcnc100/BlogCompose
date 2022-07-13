package com.nullpointer.blogcompose.domain.auth

import androidx.core.net.toUri
import com.google.firebase.auth.AuthCredential
import com.nullpointer.blogcompose.core.utils.InternetCheck
import com.nullpointer.blogcompose.core.utils.NetworkException
import com.nullpointer.blogcompose.data.local.PreferencesDataSource
import com.nullpointer.blogcompose.data.remote.auth.AuthDataSourceImpl
import com.nullpointer.blogcompose.data.remote.image.ImagesDataSourceImpl
import com.nullpointer.blogcompose.models.users.MyUser
import kotlinx.coroutines.flow.Flow

class AuthRepoImpl(
    private val authDataSource: AuthDataSourceImpl,
    private val prefDataSource: PreferencesDataSource,
    private val imagesDataSource: ImagesDataSourceImpl,
) : AuthRepository {

    override val myUser: Flow<MyUser> = prefDataSource.getUserFromProtoStore()

    override suspend fun authWithCredential(authCredential: AuthCredential) {
        // * authenticate user and save data user
        val user = authDataSource.authWithCredential(authCredential)
        prefDataSource.saveUser(user)
    }

    override suspend fun updateTokenUser(token: String) =
        authDataSource.updateTokenUser(token,null)

    override suspend fun logOut() {
        // * log out the user and deleter user saved
        authDataSource.logOut()
        prefDataSource.deleterUser()
    }

    override suspend fun uploadDataUser(urlImg: String?, name: String?) {
        if (!InternetCheck.isNetworkAvailable()) throw NetworkException()
        val updateUser = authDataSource.updateDataUser(name, urlImg)
        prefDataSource.saveUser(updateUser)
    }


    override suspend fun createNewUser(user: MyUser) {
        if (!InternetCheck.isNetworkAvailable()) throw NetworkException()
        val uriImg=imagesDataSource.uploadImageUserWithState(user.urlImg.toUri())
        val updateUser = authDataSource.updateFullDataUser(user.name,uriImg.toString())
        prefDataSource.saveUser(updateUser)
    }


}