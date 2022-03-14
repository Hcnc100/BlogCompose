package com.nullpointer.blogcompose.data.remote

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.nullpointer.blogcompose.models.User
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import timber.log.Timber

@OptIn(ExperimentalCoroutinesApi::class)
class AuthDataSource {
    private val auth = Firebase.auth
    private val nodeUsers = Firebase.firestore.collection("Users")
    val uriImgUser = auth.currentUser?.photoUrl?.toString()
    val nameUser = auth.currentUser?.displayName
    val isDataComplete = !nameUser.isNullOrBlank() && !uriImgUser.isNullOrBlank()
    val uuidUser = auth.currentUser?.uid

    suspend fun getUser(uuidUser: String = auth.currentUser!!.uid): User {
        return nodeUsers.document(uuidUser).get().await()
            .toObject(User::class.java) ?: User(uuid = uuidUser,
            token = FirebaseMessaging.getInstance().token.await())
    }

    suspend fun createUser(newUser: User) {
        nodeUsers.document(auth.currentUser!!.uid).set(newUser).await()
    }

    suspend fun updateDataUser(infoUpdate: Map<String, Any>) {
        nodeUsers.document(auth.currentUser!!.uid).update(infoUpdate).await()
    }

    fun getCurrentUser() = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener {
            val user = it.currentUser
            if (user != null) {
                trySend(user.displayName?.let { it1 ->
                    user.photoUrl?.toString()?.let { it2 ->
                        User(
                            nameUser = it1,
                            urlImg = it2,
                            uuid = user.uid
                        )
                    }
                })
            } else {
                trySend(null)
            }
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }


    suspend fun authWithTokenGoogle(token: String): User {
        val credential = GoogleAuthProvider.getCredential(token, null)
        val resultAuth = auth.signInWithCredential(credential).await()
        return getUser(resultAuth.user!!.uid)
    }


    suspend fun updateDataUser(name: String, urlImg: String) {
        val profileUpdate = userProfileChangeRequest {
            displayName = name
            photoUri = Uri.parse(urlImg)
        }
        auth.currentUser?.updateProfile(profileUpdate)?.await()
    }

    suspend fun updateImgUser(urlImg: String): String {
        val profileUpdate = userProfileChangeRequest {
            photoUri = Uri.parse(urlImg)
        }
        auth.currentUser?.updateProfile(profileUpdate)?.await()
        return auth.currentUser?.photoUrl.toString()
    }

    suspend fun updateNameUser(name: String): String {
        val profileUpdate = userProfileChangeRequest {
            displayName = name
        }
        auth.currentUser?.updateProfile(profileUpdate)?.await()
        return auth.currentUser?.displayName!!
    }

    suspend fun deleterUser() {
        auth.currentUser?.delete()?.await()
    }

    fun logOut() {
        auth.signOut()
    }


}