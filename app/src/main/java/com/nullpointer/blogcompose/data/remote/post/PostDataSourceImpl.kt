package com.nullpointer.blogcompose.data.remote.post

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.nullpointer.blogcompose.models.Comment
import com.nullpointer.blogcompose.models.notify.Notify
import com.nullpointer.blogcompose.models.posts.Post
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.util.*

class PostDataSourceImpl {

    companion object {
        private const val NAME_REF_POST = "posts"
        private const val NAME_REF_LIKE_POST = "likePost"
        private const val NAME_REF_COMMENTS = "comments"
        private const val NAME_REF_NOTIFY = "notifications"
        private const val NAME_REF_USERS_LIKE = "usersLike"
        private const val NAME_REF_LIST_COMMENTS = "listComments"
        private const val TIMESTAMP = "timestamp"
        private const val FIELD_POST_ID = "userPoster.idUser"
        private const val FIELD_NUMBER_COMMENTS = "numberComments"
        private const val FIELD_NUMBER_LIKES = "numberLikes"
        private const val NAME_REF_USERS = "users"
    }

    private val database = Firebase.firestore
    private val refPosts = database.collection(NAME_REF_POST)
    private val refLikePost = database.collection(NAME_REF_LIKE_POST)
    private val refComment = database.collection(NAME_REF_COMMENTS)
    private val refNotify = database.collection(NAME_REF_NOTIFY)
    private val refUsers = database.collection(NAME_REF_USERS)
    private val auth = Firebase.auth
    private val functions = Firebase.functions


    suspend fun addNewPost(post: Post): String {
        refPosts.document(post.id).set(post).await()
        return post.id
    }

    suspend fun getLastPostByUser(idUser: String, nPost: Int = Integer.MAX_VALUE): List<Post> {
        return getLastPost(nPost, idUser)
    }

    suspend fun getPost(idPost: String): Post? {
        val document = refPosts.document(idPost).get().await()
        return transformDocumentPost(document)
    }

    suspend fun deleterPost(idPost: String) {
        refPosts.document(idPost).delete().await()
    }


    suspend fun getLastPost(
        nPosts: Int = Int.MAX_VALUE,
        startWithPostId: String? = null,
        endWithPostId: String? = null,
        fromUserId: String? = null,
        includePost: Boolean = false,
    ): List<Post> {
        // * base query
        var query = refPosts.orderBy(TIMESTAMP, Query.Direction.DESCENDING)
        // * filter to myUser or for default get all
        if (fromUserId != null) query = query.whereEqualTo(FIELD_POST_ID, fromUserId)
        // * get only post validating
        // ? indicate if start or end with any id post
        if (startWithPostId != null) {
            val refDocument = refPosts.document(startWithPostId).get().await()
            if (refDocument.exists())
                query =
                    if (includePost) query.startAt(refDocument) else query.startAfter(refDocument)
        } else if (endWithPostId != null) {
            val refDocument = refPosts.document(endWithPostId).get().await()
            if (refDocument.exists())
                query = if (includePost) query.endAt(refDocument) else query.endBefore(refDocument)
        }
        // * limit result or for default all
        if (nPosts != Integer.MAX_VALUE) query = query.limit(nPosts.toLong())
        return query.get().await().documents.mapNotNull {
            transformDocumentPost(it)
        }
    }

    suspend fun getLastPostDate(
        date: Date? = null,
        nPosts: Int = Integer.MAX_VALUE,
        fromUserId: String? = null,
    ): List<Post> {
        // * order post for date
        var query = refPosts.orderBy(TIMESTAMP, Query.Direction.DESCENDING)
        // * get more recent post that date passed for args
        if (date != null) query = query.whereGreaterThan(TIMESTAMP, date)
        // * get only post validating
        // * filter to myUser or for default get all
        if (fromUserId != null) query = query.whereEqualTo(FIELD_POST_ID, fromUserId)
        // * limit the request
        if (nPosts != Integer.MAX_VALUE) query = query.limit(nPosts.toLong())
        return query.get().await().documents.mapNotNull {
            transformDocumentPost(it)
        }
    }


    suspend fun getMyLastPost(
        nPosts: Int = Integer.MAX_VALUE,
        startWithPostId: String? = null,
        endWithPostId: String? = null,
        includePost: Boolean = false,
    ): List<Post> {
        return getLastPost(
            nPosts = nPosts,
            startWithPostId = startWithPostId,
            endWithPostId = endWithPostId,
            fromUserId = auth.currentUser?.uid!!,
            includePost = includePost
        )
    }

    suspend fun getMyLastPostDate(
        date: Date? = null,
        nPosts: Int = Integer.MAX_VALUE,
    ): List<Post> {
        return getLastPostDate(
            date, nPosts,
            auth.currentUser!!.uid
        )
    }

    fun getRealTimePost(idPost: String) = callbackFlow {
        // * listener changes to post
        val refPost = refPosts.document(idPost)
        val listener = refPost.addSnapshotListener { value, error ->
            if (error != null) close(error)
            Timber.d("Se envio un post")
            launch { trySend(transformDocumentPost(value)) }
        }
        // ! remove listener with no any listener
        awaitClose {
            Timber.d("Se removio el listener del post $idPost")
            listener.remove()
        }
    }


    private suspend fun transformDocumentPost(documentSnapshot: DocumentSnapshot?): Post? {
        if (documentSnapshot == null) return null
        return documentSnapshot.toObject(Post::class.java)?.let { post ->
            post.apply {
                timestamp = documentSnapshot.getTimestamp(
                    TIMESTAMP,
                    DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                )?.toDate()
                ownerLike = isPostLiked(documentSnapshot.id)
                id = documentSnapshot.id
            }
        }
    }

    private suspend fun isPostLiked(idPost: String): Boolean {
        // * if document with owner id exists, then so like
        val uuid = auth.currentUser?.uid!!
        val refListLike = refLikePost.document(idPost).get().await()
        return if (refListLike.exists()) {
            (refListLike.get("likes") as List<*>).contains(uuid)
        } else false
    }

    suspend fun updateLikes(
        idPost: String,
        isLiked: Boolean,
        notify: Notify?,
        ownerPost: String,
        idUser: String
    ): Post? {
        val refPostLiked = refPosts.document(idPost)
        val refListPostLike = refLikePost.document(idPost)
        val refNotifyOwner =
            refNotify.document(ownerPost).collection("listNotify").document(notify?.id ?: "")

        database.runTransaction { transaction ->
            val snapshotLikePos = transaction.get(refListPostLike)
            if (isLiked) {
                if (snapshotLikePos.exists()) {
                    transaction.update(refListPostLike, "likes", FieldValue.arrayUnion(idUser))
                } else {
                    transaction.set(
                        refListPostLike,
                        hashMapOf("likes" to arrayListOf(idUser)),
                        SetOptions.merge()
                    )
                }
            } else {
                transaction.update(refListPostLike, "likes", FieldValue.arrayRemove(idUser))
            }
            notify?.let {
                transaction.set(refNotifyOwner, notify)
            }
            val updateCount = if (isLiked) FieldValue.increment(1) else FieldValue.increment(-1)
            transaction.update(refPostLiked, "numberLikes", updateCount)
        }.await()
        return transformDocumentPost(refPosts.document(idPost).get().await())
    }

    suspend fun getCommentsForPost(
        nComments: Int = Integer.MAX_VALUE,
        startWithCommentId: String? = null,
        endWithCommentId: String? = null,
        includeComment: Boolean = false,
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

        return query.get(Source.SERVER).await().documents.mapNotNull {
            transformDocumentToComment(it)
        }.reversed()
    }

    private fun transformDocumentToComment(documentSnapshot: DocumentSnapshot?): Comment? {
        if (documentSnapshot == null) return null
        return documentSnapshot.toObject(Comment::class.java)?.let { comment ->
            comment.apply {
                timestamp = documentSnapshot.getTimestamp(
                    TIMESTAMP,
                    DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                )?.toDate()
                id = documentSnapshot.id
            }
        }
    }


    suspend fun addNewComment(
        idPost: String,
        ownerPost: String,
        comment: Comment,
        notify: Notify?
    ): String {
        val refPostComment = refPosts.document(idPost)
        val refNewComment =
            refComment.document(idPost).collection("listComments").document(comment.id)
        val refNotifyOwner =
            refNotify.document(ownerPost).collection("listNotify").document(notify?.id ?: "")

        database.runTransaction { transaction ->
            transaction.update(refPostComment, "numberComments", FieldValue.increment(1))
            transaction.set(refNewComment, comment)
            notify?.let {
                transaction.set(refNotifyOwner, notify)
            }
        }.await()
        return refNewComment.id
    }
}