package com.nullpointer.blogcompose.data.remote.post

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.nullpointer.blogcompose.core.utils.getConcatenateObjects
import com.nullpointer.blogcompose.core.utils.getNewObjects
import com.nullpointer.blogcompose.core.utils.getTimeEstimate
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

class PostRemoteDataSourceImpl : PostRemoteDataSource {
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
        numberPost: Long,
        idPost: String?,
        fromUserId: String?,
        includePost: Boolean,
    ): List<Post> {
        return refPosts.getNewObjects(
            includeEnd = includePost,
            fieldTimestamp = TIMESTAMP,
            nResults = numberPost,
            transform = { it.toPost() },
            addingQuery = {
                if (fromUserId != null)
                    it.whereEqualTo(FIELD_POST_ID, fromUserId) else it
            }
        )
    }

    override suspend fun getConcatenatePost(
        idPost: String?,
        numberPosts: Long,
        fromUserId: String?
    ): List<Post> {
        return refPosts.getConcatenateObjects(
            includeStart = false,
            fieldTimestamp = TIMESTAMP,
            startWithId = idPost,
            nResults = numberPosts,
            transform = { it.toPost() },
            addingQuery = {
                if (fromUserId != null)
                    it.whereEqualTo(FIELD_POST_ID, fromUserId) else it
            }
        )
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
            timestamp = getTimeEstimate(TIMESTAMP),
            id = id,
        )?.also {
            it.ownerLike = isPostLiked(id)
        }
    }
}