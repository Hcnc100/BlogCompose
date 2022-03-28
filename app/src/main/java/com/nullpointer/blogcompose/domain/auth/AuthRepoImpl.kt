package com.nullpointer.blogcompose.domain.auth

import com.google.firebase.auth.AuthCredential
import com.nullpointer.blogcompose.core.utils.InternetCheck
import com.nullpointer.blogcompose.core.utils.NetworkException
import com.nullpointer.blogcompose.data.local.PreferencesDataSource
import com.nullpointer.blogcompose.data.remote.AuthDataSource
import com.nullpointer.blogcompose.models.User
import kotlinx.coroutines.flow.Flow

class AuthRepoImpl(
    private val authDataSource: AuthDataSource,
    private val prefDataSource: PreferencesDataSource,
) : AuthRepository {

    override val user: Flow<User> = prefDataSource.getUserFromProtoStore()

    override suspend fun authWithCredential(authCredential: AuthCredential) {
        val user = authDataSource.authWithCredential(authCredential)
        prefDataSource.saveUser(user)
    }

    override suspend fun updateTokenUser(token: String){
        authDataSource.updateTokenUser(token)
    }

    override suspend fun deleterUser() {
        authDataSource.deleterUser()
        prefDataSource.deleterUser()
    }

    override suspend fun uploadDataUser(urlImg: String?, name: String?) {
        if (!InternetCheck.isNetworkAvailable()) throw NetworkException()
        val updateUser = authDataSource.updateDataUser(name, urlImg)
        prefDataSource.saveUser(updateUser)
    }

    override suspend fun logOut() {
        authDataSource.logOut()
        deleterUser()
    }


}