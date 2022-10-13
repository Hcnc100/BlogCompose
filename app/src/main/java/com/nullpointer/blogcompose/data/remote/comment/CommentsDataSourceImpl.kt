package com.nullpointer.blogcompose.data.remote.comment

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.nullpointer.blogcompose.core.utils.getConcatenateObjects
import com.nullpointer.blogcompose.core.utils.getNewObjects
import com.nullpointer.blogcompose.core.utils.getTimeEstimate
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


    override suspend fun getLastCommentFromPost(
        idPost: String,
        numberComments: Long,
        includeComment: Boolean,
        idComment: String?
    ): List<Comment> {
        val refListComment = refPosts.document(idPost).collection(NAME_REF_LIST_COMMENTS)
        return refListComment.getNewObjects(
            fieldTimestamp = TIMESTAMP,
            transform = { it.toComment() },
            nResults = numberComments,
            includeEnd = includeComment,
            endWithId = idComment
        )
    }

    override suspend fun getListConcatenateComments(
        idPost: String,
        numberComments: Long,
        idComment: String
    ): List<Comment> {
        val refListComment = refPosts.document(idPost).collection(NAME_REF_LIST_COMMENTS)
        return refListComment.getConcatenateObjects(
            fieldTimestamp = TIMESTAMP,
            transform = { it.toComment() },
            nResults = numberComments,
            startWithId = idComment,
            includeStart = false
        )
    }

    override suspend fun addNewComment(
        idPost: String,
        ownerPost: String,
        comment: Comment,
        notify: Notify?
    ): String {
        val refPostComment = refPosts.document(idPost)
        val refNewComment =
            refComment.document(idPost).collection(NAME_REF_LIST_COMMENTS).document(comment.id)
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
            timestamp = getTimeEstimate(TIMESTAMP),
            id = id,
        )
    }
}