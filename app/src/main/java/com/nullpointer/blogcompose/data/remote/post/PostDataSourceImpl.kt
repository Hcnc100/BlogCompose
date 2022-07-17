package com.nullpointer.blogcompose.data.remote.post

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.nullpointer.blogcompose.core.utils.timestampEstimate
import com.nullpointer.blogcompose.data.remote.FirebaseConstants.FIELD_ARRAY_LIKES
import com.nullpointer.blogcompose.data.remote.FirebaseConstants.FIELD_NUMBER_LIKES
import com.nullpointer.blogcompose.data.remote.FirebaseConstants.FIELD_POST_ID
import com.nullpointer.blogcompose.data.remote.FirebaseConstants.NAME_REF_LIKE_POST
import com.nullpointer.blogcompose.data.remote.FirebaseConstants.NAME_REF_LIST_NOTIFY
import com.nullpointer.blogcompose.data.remote.FirebaseConstants.NAME_REF_NOTIFY
import com.nullpointer.blogcompose.data.remote.FirebaseConstants.NAME_REF_POST
import com.nullpointer.blogcompose.data.remote.FirebaseConstants.TIMESTAMP
import com.nullpointer.blogcompose.models.notify.Notify
import com.nullpointer.blogcompose.models.posts.Post
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.util.*

class PostDataSourceImpl:PostDataSource {
    private val database = Firebase.firestore
    private val refPosts = database.collection(NAME_REF_POST)
    private val refLikePost = database.collection(NAME_REF_LIKE_POST)
    private val refNotify = database.collection(NAME_REF_NOTIFY)
    private val auth = Firebase.auth


    override suspend fun addNewPost(post: Post): String {
        refPosts.document(post.id).set(post).await()
        return post.id
    }

    override suspend fun deleterPost(idPost: String) {
        refPosts.document(idPost).delete().await()
    }

    override suspend fun getPost(idPost: String): Post? {
        return refPosts.document(idPost).get().await().toPost()
    }


    override suspend fun getLastPost(
        nPosts: Int,
        startWithPostId: String?,
        endWithPostId: String?,
        fromUserId: String?,
        includePost: Boolean,
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
                query = if (includePost) query.startAt(refDocument) else query.startAfter(refDocument)
        } else if (endWithPostId != null) {
            val refDocument = refPosts.document(endWithPostId).get().await()
            if (refDocument.exists())
                query = if (includePost) query.endAt(refDocument) else query.endBefore(refDocument)
        }
        // * limit result or for default all
        if (nPosts != Integer.MAX_VALUE) query = query.limit(nPosts.toLong())
        return query.get().await().documents.mapNotNull { it.toPost() }
    }

    override suspend fun getLastPostBeforeThat(
        date: Date?,
        nPosts: Int,
        fromUserId: String?,
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
        return query.get().await().documents.mapNotNull { it.toPost() }
    }

    override fun getRealTimePost(idPost: String): Flow<Post?> = callbackFlow {
        // * listener changes to post
        val refPost = refPosts.document(idPost)
        val listener = refPost.addSnapshotListener { value, error ->
            if (error != null) close(error)
            Timber.d("Se envio un post")
            launch { trySend(value?.toPost()) }
        }
        // ! remove listener with no any listener
        awaitClose {
            Timber.d("Se removio el listener del post $idPost")
            listener.remove()
        }
    }




    private suspend fun isPostLiked(idPost: String): Boolean {
        // * if document with owner id exists, then so like
        val uuid = auth.currentUser?.uid!!
        val refListLike = refLikePost.document(idPost).get().await()
        return if (refListLike.exists()) {
            (refListLike.get(FIELD_ARRAY_LIKES) as List<*>).contains(uuid)
        } else false
    }

    override suspend fun updateLikes(
        idPost: String,
        isLiked: Boolean,
        notify: Notify?,
        ownerPost: String,
        idUser: String
    ): Post? {
        val refPostLiked = refPosts.document(idPost)
        val refListPostLike = refLikePost.document(idPost)
        val refListNotifyOwnerPost = refNotify.document(ownerPost).collection(NAME_REF_LIST_NOTIFY)

        database.runTransaction { transaction ->
            val snapshotLikePos = transaction.get(refListPostLike)
            if (isLiked) {
                if (snapshotLikePos.exists()) {
                    transaction.update(
                        refListPostLike,
                        FIELD_ARRAY_LIKES,
                        FieldValue.arrayUnion(idUser)
                    )
                } else {
                    transaction.set(
                        refListPostLike,
                        hashMapOf(FIELD_ARRAY_LIKES to arrayListOf(idUser)),
                        SetOptions.merge()
                    )
                }
            } else {
                transaction.update(
                    refListPostLike,
                    FIELD_ARRAY_LIKES,
                    FieldValue.arrayRemove(idUser)
                )
            }
            notify?.let {
                transaction.set(refListNotifyOwnerPost.document(it.id), notify)
            }
            val updateCount = if (isLiked) FieldValue.increment(1) else FieldValue.increment(-1)
            transaction.update(refPostLiked, FIELD_NUMBER_LIKES, updateCount)
        }.await()
        return refPosts.document(idPost).get().await().toPost()
    }



    private suspend fun DocumentSnapshot.toPost(): Post? {
        return toObject<Post>()?.copy(
            timestamp = timestampEstimate(TIMESTAMP),
            id = id,
            ownerLike = isPostLiked(id)
        )
    }
}