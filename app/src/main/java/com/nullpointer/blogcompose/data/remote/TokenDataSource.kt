package com.nullpointer.blogcompose.data.remote

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class TokenDataSource {
    private val database = Firebase.firestore.collection("tokens")
    private val auth = Firebase.auth
    suspend fun updateTokenUser(token: String) {
        database.document(auth.currentUser!!.uid).set(mapOf("token" to token)).await()
    }
}