package com.nullpointer.blogcompose.data.remote

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.Source
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

    suspend fun getLastNotifications(
        afterId: String? = null,
        beforeId: String? = null,
        nNotify: Int = Integer.MAX_VALUE,
    ): List<Notify> {
        var baseQuery = database.document(auth.currentUser?.uid!!)
            .collection("listNotify").orderBy("timestamp", Query.Direction.DESCENDING)
        // * get documents after that
        if (afterId != null) {
            val lastDocument = database.document(auth.currentUser?.uid!!)
                .collection("listNotify").document(afterId).get(Source.CACHE).await()
            baseQuery = baseQuery.startAfter(lastDocument)
        }
        if (beforeId != null) {
            val lastDocument = database.document(auth.currentUser?.uid!!)
                .collection("listNotify").document(beforeId).get(Source.CACHE).await()
            baseQuery = baseQuery.endBefore(lastDocument)
        }
        // * limit result or for default all
        if (nNotify != Integer.MAX_VALUE) baseQuery = baseQuery.limit(nNotify.toLong())
        return baseQuery.get(Source.SERVER).await().documents.mapNotNull { document ->
            document.toObject(Notify::class.java)?.apply {
                id = document.id
                timestamp = document.getTimestamp(
                    "timestamp",
                    DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                )?.toDate()
            }
        }
    }

}