package com.nullpointer.blogcompose.data.remote

import android.net.Uri
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.nullpointer.blogcompose.models.User
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalCoroutinesApi::class)
class AuthDataSource {
    private val auth = Firebase.auth
    private val nodeUsers = Firebase.firestore.collection("users")

    suspend fun createUser(newUser: User) {
        nodeUsers.document(auth.currentUser!!.uid).set(newUser).await()
    }

    suspend fun updateDataUser(infoUpdate: Map<String, Any>) {
        nodeUsers.document(auth.currentUser!!.uid).update(infoUpdate).await()
    }

    suspend fun authWithCredential(credential: AuthCredential): User {
        val resultAuth = auth.signInWithCredential(credential).await()
        // ! when susses auth update token in database
        val nodeUser = nodeUsers.document(resultAuth.user!!.uid)
        val nodeResult = nodeUser.get().await()
        return if (nodeResult.exists()) {
            val mapUpdate = mapOf(
                "timeUpdate" to FieldValue.serverTimestamp(),
                "token" to FirebaseMessaging.getInstance().token.await()
            )
            nodeResult.toObject(User::class.java)!!
        } else {
            User()
        }.apply {
            idUser = resultAuth.user!!.uid
        }
    }

    suspend fun updateTokenUser(newToken: String) {
        val mapUpdate = mapOf(
            "timeUpdate" to FieldValue.serverTimestamp(),
            "token" to FirebaseMessaging.getInstance().token.await()
        )
        nodeUsers.document(auth.currentUser!!.uid).update(mapUpdate).await()
    }


    suspend fun updateDataUser(name: String?, urlImg: String?): User {
        val nodeUser = nodeUsers.document(auth.currentUser!!.uid)
        val nodeResult = nodeUser.get().await()
        if (nodeResult.exists()) {
            val mapUpdate = mutableMapOf<String, Any>()
            name?.let { mapUpdate["nameUser"] = name }
            urlImg?.let { mapUpdate["urlImg"] = urlImg }
            mapUpdate["token"] = FirebaseMessaging.getInstance().token.await()
            mapUpdate["timeUpdate"] = FieldValue.serverTimestamp()
            nodeUser.update(mapUpdate).await()
        } else {
            val newUser = User(
                token = FirebaseMessaging.getInstance().token.await(),
                urlImg = urlImg!!,
                nameUser = name!!
            )
            nodeUsers.document(auth.currentUser!!.uid).set(newUser).await()
        }
        return nodeUser.get().await().toObject(User::class.java)!!.apply {
            idUser = auth.currentUser?.uid ?: ""
        }

    }



    suspend fun deleterUser() {
        auth.currentUser?.delete()?.await()
    }

    fun logOut() {
        auth.signOut()
    }


}