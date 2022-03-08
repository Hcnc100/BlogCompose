package com.nullpointer.blogcompose.data.remote

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.nullpointer.blogcompose.models.Notify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.lang.Exception

class NotifyDataSource {
    private val auth = Firebase.auth
    private val database = Firebase.firestore.collection("notifications")

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun getListNotify(): List<Notify> {
        return database.document(auth.currentUser?.uid!!)
            .collection("listNotify").orderBy("timestamp")
            .get().await().documents.mapNotNull { document ->
                document.toObject(Notify::class.java)?.apply {
                    id = document.id
                }
            }
    }

}