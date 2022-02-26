package com.nullpointer.blogcompose.data.remote

import android.net.Credentials
import android.net.Uri
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class AuthDataSource {
    private val auth = Firebase.auth
    val isDataComplete get() = nameUser.isNullOrBlank() && uriImgUser != null
    val uriImgUser get() =  auth.currentUser?.photoUrl
    val nameUser get() = auth.currentUser?.displayName
    val uuidUser get() = auth.currentUser?.uid


    suspend fun authWithTokeGoogle(token: String) {
        val credential = GoogleAuthProvider.getCredential(token, null)
        auth.signInWithCredential(credential).await()
    }

    suspend fun updateDataUser(name: String, urlImg: String) {
        val profileUpdate = userProfileChangeRequest {
            displayName = name
            photoUri = Uri.parse(urlImg)
        }
        auth.currentUser?.updateProfile(profileUpdate)?.await()
    }

    suspend fun updateImgUser(urlImg: String) {
        val profileUpdate = userProfileChangeRequest {
            photoUri = Uri.parse(urlImg)
        }
        auth.currentUser?.updateProfile(profileUpdate)?.await()
    }

    suspend fun updateNameUser(name: String) {
        val profileUpdate = userProfileChangeRequest {
            displayName = name
        }
        auth.currentUser?.updateProfile(profileUpdate)?.await()
    }

    suspend fun deleterUser(){
        auth.currentUser?.delete()?.await()
    }

     fun logOut(){
        auth.signOut()
    }


}