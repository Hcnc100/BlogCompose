package com.nullpointer.blogcompose.data.remote

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.nullpointer.blogcompose.models.Notify
import kotlinx.coroutines.tasks.await
import java.util.*

class NotifyDataSource {
    companion object {
        private const val TIMESTAMP = "timestamp"
        private const val NOTIFICATIONS = "notifications"
        private const val LIST_NOTIFY = "listNotify"
        private const val FIELD_IS_OPEN = "isOpen"
    }

    private val auth = Firebase.auth
    private val nodeNotify = Firebase.firestore.collection(NOTIFICATIONS)

    suspend fun getLastNotifications(
        startWith: String? = null,
        endWith: String? = null,
        numberRequest: Int = Integer.MAX_VALUE,
    ): List<Notify> {
        // * get last notify consideration the id passed from parameter, this for no reload all
        // * else just return the request number of notify, this is "pagination"
        val nodeUserNotify = nodeNotify.document(auth.currentUser?.uid!!).collection(LIST_NOTIFY)
        // * order for timestamp
        var baseQuery = nodeUserNotify.orderBy(TIMESTAMP, Query.Direction.DESCENDING)
        // ? if passed id, indicate start, request notifications, that initial with this
        // ? or get request notifications that end with notifications
        if (startWith != null) {
            val refDocument = nodeUserNotify.document(startWith).get(Source.CACHE).await()
            if (refDocument.exists())
                baseQuery = baseQuery.startAfter(refDocument)
        } else if (endWith != null) {
            val refDocument = nodeUserNotify.document(endWith).get(Source.CACHE).await()
            if (refDocument.exists())
                baseQuery = baseQuery.endBefore(refDocument)
        }
        // * limit result or for default all
        if (numberRequest != Integer.MAX_VALUE) baseQuery = baseQuery.limit(numberRequest.toLong())
        // * transform document to notifications
        return baseQuery.get(Source.SERVER).await().documents.mapNotNull { document ->
            transformDocumentInNotify(document)
        }
    }

    suspend fun getLastNotifyDate(numberRequest: Int, date: Date?): List<Notify> {
        // * node of notifications
        val nodeUserNotify = nodeNotify.document(auth.currentUser?.uid!!).collection(LIST_NOTIFY)
        // * order for timestamp
        var baseQuery = nodeUserNotify.orderBy(TIMESTAMP, Query.Direction.DESCENDING)
        // * search get notifications more recently if pass date
        if (date != null) baseQuery = baseQuery.whereGreaterThan(TIMESTAMP, date)
        // * limit result or get all notifications (no recommended)
        if (numberRequest != Integer.MAX_VALUE) baseQuery = baseQuery.limit(numberRequest.toLong())
        // * transform result in notify
        return baseQuery.get(Source.SERVER).await().documents.mapNotNull { document ->
            transformDocumentInNotify(document)
        }
    }

    private fun transformDocumentInNotify(document: DocumentSnapshot): Notify? {
        // * transform the document in notify
        // ? adding id and timestamp estimate
        return document.toObject(Notify::class.java)?.apply {
            id = document.id
            // ! i dont know becose no update this field
            isOpen = document.getBoolean(FIELD_IS_OPEN)!!
            timestamp = document
                .getTimestamp(TIMESTAMP, DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                )?.toDate()
        }
    }

    fun updateOpenNotify(idNotify: String) {
        val nodeUserNotify = nodeNotify.document(auth.currentUser?.uid!!).collection(LIST_NOTIFY)
        nodeUserNotify.document(idNotify).update(FIELD_IS_OPEN, true)
    }


}