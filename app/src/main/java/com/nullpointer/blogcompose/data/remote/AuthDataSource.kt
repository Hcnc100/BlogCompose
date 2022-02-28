package com.nullpointer.blogcompose.data.remote

import android.net.Credentials
import android.net.Uri
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class AuthDataSource {
    private val auth = Firebase.auth
    val uriImgUser = auth.currentUser?.photoUrl?.toString()
    val nameUser = auth.currentUser?.displayName
    val isDataComplete = !nameUser.isNullOrBlank() && !uriImgUser.isNullOrBlank()
    val uuidUser = auth.currentUser?.uid


    suspend fun authWithTokenGoogle(token: String) = callbackFlow {
        val credential = GoogleAuthProvider.getCredential(token, null)
        auth.signInWithCredential(credential).await()
        // ! await to complete data user for know if data is complete
        val listener = FirebaseAuth.AuthStateListener {
            val user = it.currentUser
            if (user != null) {
                val name = user.displayName
                val urlImg = user.photoUrl?.toString()
                trySend(Pair( name, urlImg))
                close()
            }
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
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

    suspend fun deleterUser() {
        auth.currentUser?.delete()?.await()
    }

    fun logOut() {
        auth.signOut()
    }


}