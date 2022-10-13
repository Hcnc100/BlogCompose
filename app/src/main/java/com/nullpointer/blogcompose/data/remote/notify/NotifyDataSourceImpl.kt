package com.nullpointer.blogcompose.data.remote.notify

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.nullpointer.blogcompose.core.utils.getTimeEstimate
import com.nullpointer.blogcompose.models.notify.Notify
import kotlinx.coroutines.tasks.await
import java.util.*

class NotifyDataSourceImpl:NotifyDataSource{
    companion object {
        private const val TIMESTAMP = "timestamp"
        private const val NOTIFICATIONS = "notifications"
        private const val LIST_NOTIFY = "listNotify"
        private const val FIELD_IS_OPEN = "isOpen"
    }

    private val auth = Firebase.auth
    private val nodeNotify = Firebase.firestore.collection(NOTIFICATIONS)

    override suspend fun getLastNotifications(
        startWith: String?,
        endWith: String?,
        numberRequest: Int,
        includeNotify:Boolean
    ): List<Notify> {
        // * get last notify consideration the id passed from parameter, this for no reload all
        // * else just return the request number of notify, this is "pagination"
        val nodeUserNotify = nodeNotify.document(auth.currentUser?.uid!!).collection(LIST_NOTIFY)
        // * order for timestamp
        var baseQuery = nodeUserNotify.orderBy(TIMESTAMP, Query.Direction.DESCENDING)
        // ? if passed id, indicate start, request notifications, that initial with this
        // ? or get request notifications that end with notifications
        if (startWith != null) {
            val refDocument = nodeUserNotify.document(startWith).get().await()
            if (refDocument.exists())
                baseQuery = if (includeNotify) baseQuery.startAt(refDocument) else baseQuery.startAfter(refDocument)
        } else if (endWith != null) {
            val refDocument = nodeUserNotify.document(endWith).get().await()
            if (refDocument.exists())
                baseQuery =  if (includeNotify) baseQuery.endAt(refDocument) else baseQuery.endBefore(refDocument)
        }
        // * limit result or for default all
        if (numberRequest != Integer.MAX_VALUE) baseQuery = baseQuery.limit(numberRequest.toLong())
        // * transform document to notifications
        return baseQuery.get().await().documents.mapNotNull { it.toNotify() }
    }

    override suspend fun getLastNotifyBeforeThat(numberRequest: Int, date: Date?): List<Notify> {
        // * node of notifications
        val nodeUserNotify = nodeNotify.document(auth.currentUser?.uid!!).collection(LIST_NOTIFY)
        // * order for timestamp
        var baseQuery = nodeUserNotify.orderBy(TIMESTAMP, Query.Direction.DESCENDING)
        // * search get notifications more recently if pass date
        if (date != null) baseQuery = baseQuery.whereGreaterThan(TIMESTAMP, date)
        // * limit result or get all notifications (no recommended)
        if (numberRequest != Integer.MAX_VALUE) baseQuery = baseQuery.limit(numberRequest.toLong())
        // * transform result in notify
        return baseQuery.get().await().documents.mapNotNull{ it.toNotify() }
    }

    override fun updateOpenNotify(idNotify: String) {
        val nodeUserNotify = nodeNotify.document(auth.currentUser?.uid!!).collection(LIST_NOTIFY)
        nodeUserNotify.document(idNotify).update(FIELD_IS_OPEN, true)
    }


    private fun DocumentSnapshot.toNotify(): Notify? {
        return toObject<Notify>()?.copy(
            id = id,
            timestamp = getTimeEstimate(TIMESTAMP)
        )
    }


}