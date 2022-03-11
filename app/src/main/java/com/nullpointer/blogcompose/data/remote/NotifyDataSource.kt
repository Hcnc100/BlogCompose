package com.nullpointer.blogcompose.data.remote

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.nullpointer.blogcompose.models.Notify
import kotlinx.coroutines.tasks.await

class NotifyDataSource {
    companion object {
        private const val TIMESTAMP = "timestamp"
        private const val NOTIFICATIONS = "notifications"
        private const val LIST_NOTIFY = "listNotify"
    }

    private val auth = Firebase.auth
    private val nodeNotify = Firebase.firestore.collection(NOTIFICATIONS)

    suspend fun getLastNotifications(
        afterId: String? = null,
        beforeId: String? = null,
        numberRequest: Int = Integer.MAX_VALUE,
    ): List<Notify> {
        // * get last notify consideration the id passed from parameter, this for no reload all
        // * else just return the request number of notify, this is "pagination"

        val nodeUserNotify = nodeNotify.document(auth.currentUser?.uid!!).collection(LIST_NOTIFY)

        var baseQuery = nodeUserNotify.orderBy(TIMESTAMP, Query.Direction.DESCENDING)
        if (afterId != null) {
            val lastDocument = nodeUserNotify.document(afterId).get(Source.CACHE).await()
            baseQuery = baseQuery.startAfter(lastDocument)
        }
        if (beforeId != null) {
            val lastDocument = nodeUserNotify.document(beforeId).get(Source.CACHE).await()
            baseQuery = baseQuery.endBefore(lastDocument)
        }
        // * limit result or for default all
        if (numberRequest != Integer.MAX_VALUE) baseQuery = baseQuery.limit(numberRequest.toLong())

        return baseQuery.get(Source.SERVER).await().documents.mapNotNull { document ->
            transformDocumentInNotify(document)
        }
    }

    private fun transformDocumentInNotify(document: DocumentSnapshot): Notify? {
        // * transform the document in notify
        return document.toObject(Notify::class.java)?.apply {
            id = document.id
            timestamp = document.getTimestamp(
                TIMESTAMP,
                DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
            )?.toDate()
        }
    }

}