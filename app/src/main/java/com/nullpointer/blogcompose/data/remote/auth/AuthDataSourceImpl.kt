package com.nullpointer.blogcompose.data.remote.auth

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import com.nullpointer.blogcompose.models.users.MyUser
import kotlinx.coroutines.tasks.await

class AuthDataSourceImpl : AuthDataSource {

    companion object {
        private const val NAME_REF_USERS = "users"
        private const val FIELD_TIME_UPDATE = "timeUpdate"
        private const val FIELD_TOKEN = "token"
        private const val FIELD_TIME_CREATE = "timeCreate"
        private const val FIELD_URL_IMG = "urlImg"
        private const val FIELD_EMAIL = "email"
        private const val FIELD_NAME = "name"
    }

    private val auth = Firebase.auth
    private val nodeUsers = Firebase.firestore.collection(NAME_REF_USERS)


    override suspend fun authWithCredential(credential: AuthCredential): MyUser {
        val resultAuth = auth.signInWithCredential(credential).await()
        updateTokenUser(uuidUser = resultAuth.user!!.uid)
        return nodeUsers.document(resultAuth.user!!.uid).toMyUser()
    }

    override suspend fun updateTokenUser(
        token: String?,
        uuidUser: String?
    ) {
        val idDocument = uuidUser ?: auth.currentUser?.uid!!
        val refNodeUser = nodeUsers.document(idDocument)
        val docUser = refNodeUser.get().await()
        val finishToken = token ?: Firebase.messaging.token.await()
        if (docUser.exists()) {
            refNodeUser.update(
                mapOf(
                    FIELD_TIME_UPDATE to FieldValue.serverTimestamp(),
                    FIELD_TOKEN to finishToken
                )
            ).await()
        } else {
            refNodeUser.set(
                mapOf(
                    FIELD_TIME_UPDATE to FieldValue.serverTimestamp(),
                    FIELD_TIME_CREATE to FieldValue.serverTimestamp(),
                    FIELD_TOKEN to finishToken
                )
            ).await()
        }
    }


    override suspend fun updateDataUser(name: String?, urlImg: String?): MyUser {
        val nodeUser = nodeUsers.document(auth.currentUser!!.uid)
        val mapUpdate = mutableMapOf<String, Any>()
        name?.let { mapUpdate[FIELD_NAME] = name }
        urlImg?.let { mapUpdate[FIELD_URL_IMG] = urlImg }
        mapUpdate[FIELD_TIME_UPDATE] = FieldValue.serverTimestamp()
        nodeUser.update(mapUpdate).await()
        return nodeUser.toMyUser()
    }

    override suspend fun updateFullDataUser(name: String, urlImg: String): MyUser {
        val nodeUser = nodeUsers.document(auth.currentUser!!.uid)
        val dataUpdate = mapOf<String, Any>(
            FIELD_NAME to name,
            FIELD_URL_IMG to urlImg,
            FIELD_EMAIL to auth.currentUser?.email!!,

        )
        nodeUser.update(dataUpdate).await()
        return nodeUser.toMyUser()
    }

    override suspend fun deleterUser() {
        auth.currentUser?.delete()?.await()
    }

    override fun logOut() {
        auth.signOut()
    }

    private suspend fun DocumentReference.toMyUser(): MyUser{
        return get().await().let {
            it.toObject<MyUser>()!!.copy(idUser = it.id)
        }
    }
}


