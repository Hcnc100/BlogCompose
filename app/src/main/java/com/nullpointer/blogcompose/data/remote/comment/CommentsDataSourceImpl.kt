package com.nullpointer.blogcompose.data.remote.comment

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.nullpointer.blogcompose.core.utils.timestampEstimate
import com.nullpointer.blogcompose.data.remote.FirebaseConstants.FIELD_NUMBER_COMMENTS
import com.nullpointer.blogcompose.data.remote.FirebaseConstants.NAME_REF_COMMENTS
import com.nullpointer.blogcompose.data.remote.FirebaseConstants.NAME_REF_LIST_COMMENTS
import com.nullpointer.blogcompose.data.remote.FirebaseConstants.NAME_REF_LIST_NOTIFY
import com.nullpointer.blogcompose.data.remote.FirebaseConstants.NAME_REF_NOTIFY
import com.nullpointer.blogcompose.data.remote.FirebaseConstants.NAME_REF_POST
import com.nullpointer.blogcompose.data.remote.FirebaseConstants.TIMESTAMP
import com.nullpointer.blogcompose.models.Comment
import com.nullpointer.blogcompose.models.notify.Notify
import kotlinx.coroutines.tasks.await

class CommentsDataSourceImpl:CommentsDataSource {
    private val database = Firebase.firestore
    private val refPosts = database.collection(NAME_REF_POST)
    private val refComment = database.collection(NAME_REF_COMMENTS)
    private val refNotify = database.collection(NAME_REF_NOTIFY)

    override suspend fun getCommentsForPost(
        nComments: Int,
        startWithCommentId: String?,
        endWithCommentId: String?,
        includeComment: Boolean,
        idPost: String,
    ): List<Comment> {

        // * get base query
        var query = refComment
            .document(idPost)
            .collection(NAME_REF_LIST_COMMENTS)
            .orderBy(TIMESTAMP, Query.Direction.DESCENDING)

        val refCommentCurrent = refComment.document(idPost).collection(NAME_REF_LIST_COMMENTS)

        if (startWithCommentId != null) {
            val refDocument = refCommentCurrent.document(startWithCommentId).get().await()
            if (refDocument.exists())
                query =
                    if (includeComment) query.startAt(refDocument) else query.startAfter(refDocument)
        }

        if (endWithCommentId != null) {
            val refDocument = refCommentCurrent.document(endWithCommentId).get().await()
            if (refDocument.exists())
                query =
                    if (includeComment) query.endAt(refDocument) else query.endBefore(refDocument)
        }

        // * limit result or for default all
        if (nComments != Integer.MAX_VALUE) query = query.limit(nComments.toLong())

        return query.get(Source.SERVER).await().documents.mapNotNull { it.toComment() }.reversed()
    }

    override suspend fun addNewComment(
        idPost: String,
        ownerPost: String,
        comment: Comment,
        notify: Notify?
    ): String {
        val refPostComment = refPosts.document(idPost)
        val refNewComment = refComment.document(idPost).collection(NAME_REF_LIST_COMMENTS).document(comment.id)
        val refListNotifyOwnerPost = refNotify.document(ownerPost).collection(NAME_REF_LIST_NOTIFY)
        database.runTransaction { transaction ->
            transaction.update(refPostComment, FIELD_NUMBER_COMMENTS, FieldValue.increment(1))
            transaction.set(refNewComment, comment)
            notify?.let {
                transaction.set(refListNotifyOwnerPost.document(it.id), notify)
            }
        }.await()
        return refNewComment.id
    }

    private fun DocumentSnapshot.toComment(): Comment? {
        return toObject<Comment>()?.copy(
            timestamp = timestampEstimate(TIMESTAMP),
            id = id,
        )
    }
}