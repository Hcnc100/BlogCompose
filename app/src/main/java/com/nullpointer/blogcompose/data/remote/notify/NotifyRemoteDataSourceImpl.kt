package com.nullpointer.blogcompose.data.remote.notify

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.nullpointer.blogcompose.core.utils.getConcatenateObjects
import com.nullpointer.blogcompose.core.utils.getNewObjects
import com.nullpointer.blogcompose.core.utils.getTimeEstimate
import com.nullpointer.blogcompose.models.notify.Notify

class NotifyRemoteDataSourceImpl : NotifyRemoteDataSource {
    companion object {
        private const val TIMESTAMP = "timestamp"
        private const val NOTIFICATIONS = "notifications"
        private const val LIST_NOTIFY = "listNotify"
        private const val FIELD_IS_OPEN = "isOpen"
    }

    private val auth = Firebase.auth
    private val nodeNotify = Firebase.firestore.collection(NOTIFICATIONS)

    override suspend fun getLastNotifications(
        idNotify: String?,
        numberRequest: Long,
        includeNotify: Boolean
    ): List<Notify> {
        val nodeUserNotify = nodeNotify.document(auth.currentUser?.uid!!).collection(LIST_NOTIFY)
        return nodeUserNotify.getNewObjects(
            includeEnd = includeNotify,
            fieldTimestamp = TIMESTAMP,
            endWithId = idNotify,
            nResults = numberRequest,
            transform = { it.toNotify() }
        )
    }


    override suspend fun getConcatenateNotify(
        numberRequest: Long,
        idNotify: String
    ): List<Notify> {
        val nodeUserNotify = nodeNotify.document(auth.currentUser?.uid!!).collection(LIST_NOTIFY)
        return nodeUserNotify.getConcatenateObjects(
            includeStart = false,
            fieldTimestamp = TIMESTAMP,
            startWithId = idNotify,
            nResults = numberRequest,
            transform = { it.toNotify() }
        )
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