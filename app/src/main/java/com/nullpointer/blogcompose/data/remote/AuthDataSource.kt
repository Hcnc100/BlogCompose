package com.nullpointer.blogcompose.data.remote

import android.net.Uri
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.ktx.storage
import com.nullpointer.blogcompose.models.users.MyUser
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.tasks.await
import timber.log.Timber

@OptIn(ExperimentalCoroutinesApi::class)
class AuthDataSource {


    private val auth = Firebase.auth
    private val nodeUsers = Firebase.firestore.collection("users")
    private val refImgUser = Firebase.storage.getReference("imgUsers")

    suspend fun createUser(newMyUser: MyUser) {
        nodeUsers.document(auth.currentUser!!.uid).set(newMyUser).await()
    }

    suspend fun updateDataUser(infoUpdate: Map<String, Any>) {
        nodeUsers.document(auth.currentUser!!.uid).update(infoUpdate).await()
    }

    suspend fun authWithCredential(credential: AuthCredential): MyUser {
        var userSaved: MyUser? = null
        // * auth and await response
        val resultAuth = auth.signInWithCredential(credential).await()
        // * try get document user
        val nodeUser = nodeUsers.document(resultAuth.user!!.uid).get().await()
        // * if exists so try get user saved
        if (nodeUser.exists()) {
            updateTokenUser(FirebaseMessaging.getInstance().token.await())
            userSaved = nodeUser.toObject(MyUser::class.java)
        }
        // * if user no exists, but the auth is sucessfully so, get empty user
        // * and update current id
        // ! the id user is saved and is import for
        // ! know if the user is auth or not
        return (userSaved ?: MyUser()).apply {
            idUser = resultAuth.user!!.uid
            nameUser = resultAuth.user!!.displayName.toString()
        }
    }

    suspend fun updateTokenUser(newToken: String) {
        val mapUpdate = mapOf(
            "timeUpdate" to FieldValue.serverTimestamp(),
            "token" to newToken
        )
        nodeUsers.document(auth.currentUser!!.uid).update(mapUpdate).await()
    }


    suspend fun updateDataUser(name: String?, urlImg: String?): MyUser {
        val profileUpdate = userProfileChangeRequest {
            name?.let { displayName = it }
            urlImg?.let { photoUri = Uri.parse(it) }
        }
        auth.currentUser?.updateProfile(profileUpdate)?.await()
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
            val newUser = MyUser(
                token = FirebaseMessaging.getInstance().token.await(),
                urlImg = urlImg.toString(),
                nameUser = name.toString(),
                emailUser = auth.currentUser?.email.toString()
            )
            nodeUsers.document(auth.currentUser!!.uid).set(newUser).await()
        }
        return nodeUser.get().await().toObject(MyUser::class.java)!!.apply {
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
