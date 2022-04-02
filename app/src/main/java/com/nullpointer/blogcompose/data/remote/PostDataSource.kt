package com.nullpointer.blogcompose.data.remote

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.nullpointer.blogcompose.models.Comment
import com.nullpointer.blogcompose.models.posts.Post
import com.nullpointer.blogcompose.models.posts.StatePost
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.util.*

class PostDataSource {

    companion object {
        private const val NAME_REF_POST = "posts"
        private const val NAME_REF_LIKE_POST = "likePost"
        private const val NAME_REF_COMMENTS = "comments"
        private const val NAME_REF_USERS_LIKE = "usersLike"
        private const val NAME_REF_LIST_COMMENTS = "listComments"
        private const val TIMESTAMP = "timestamp"
        private const val FIELD_POST_ID = "userPoster.idUser"
        private const val FIELD_NUMBER_COMMENTS = "numberComments"
        private const val FIELD_NUMBER_LIKES = "numberLikes"
        private const val NAME_REF_USERS = "users"
        private const val NAME_FIELD_VALIDATE="stateValidate"
    }

    private val database = Firebase.firestore
    private val refPosts = database.collection(NAME_REF_POST)
    private val refLikePost = database.collection(NAME_REF_LIKE_POST)
    private val refComment = database.collection(NAME_REF_COMMENTS)
    private val refUsers = database.collection(NAME_REF_USERS)
    private val auth = Firebase.auth
    private val functions=Firebase.functions

    suspend fun addNewPost(post: Post) {
        val newPostRef = refPosts.document(post.id)
        val nodeUser = refUsers.document(auth.currentUser!!.uid)
        database.runTransaction { transition ->
            transition.set(newPostRef, post)
            val refPost = newPostRef.path + post.id
            transition.update(nodeUser,
                "listRefPost",
                FieldValue.arrayUnion(refPost))
        }.await()
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
        query=query.whereEqualTo(NAME_FIELD_VALIDATE,StatePost.VALIDATED)
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
        query=query.whereEqualTo(NAME_FIELD_VALIDATE,StatePost.VALIDATED)
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
            includePost = includePost)
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

    @OptIn(ExperimentalCoroutinesApi::class)
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
                timestamp = documentSnapshot.getTimestamp(TIMESTAMP,
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
        val refLikePost =
            refLikePost.document(idPost).collection(NAME_REF_USERS_LIKE).document(uuid)
        return refLikePost.get().await().exists()
    }

    suspend fun updateLikes(idPost: String, isLiked: Boolean): Post? {
        // * var to increment o decrement number likes
        val increment = FieldValue.increment(1)
        val decrement = FieldValue.increment(-1)

        val currentPost = refPosts.document(idPost)
        val uuid = auth.currentUser?.uid ?: ""
        // * var to save the success operation
        var isSuccess = true
        val refLikePost =
            refLikePost.document(idPost).collection(NAME_REF_USERS_LIKE).document(uuid)

        database.runTransaction { transition ->
            if (isLiked) {
                // * if the myUser liked post, so
                // * create one document with the id of the myUser and save the time when liked
                // * add one to number of like
                transition.set(refLikePost, mapOf(TIMESTAMP to FieldValue.serverTimestamp()))
                transition.update(currentPost, FIELD_NUMBER_LIKES, increment)
            } else {
                // * if the myUser no like this, so
                // * deleter document of myUser liked adn decrement number of like in this post
                transition.update(currentPost, FIELD_NUMBER_LIKES, decrement)
                transition.delete(refLikePost)
            }

        }.addOnFailureListener {
            isSuccess = false
        }.await()
        // * if the operation is success to get post with data updated, else return null
        return if (isSuccess) transformDocumentPost(currentPost.get().await()) else null
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

        query=query.whereEqualTo(NAME_FIELD_VALIDATE,StatePost.VALIDATED)

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
                query = if (includeComment) query.endAt(refDocument) else query.endBefore(refDocument)
        }

        // * limit result or for default all
        if (nComments != Integer.MAX_VALUE) query = query.limit(nComments.toLong())

        return query.get(Source.SERVER).await().documents.mapNotNull {
            transformDocumentToComment(it)
        }
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

    suspend fun addNewComment(idPost: String, newComment: Comment): String {
        val increment = FieldValue.increment(1)
        val currentPost = refPosts.document(idPost)
        val userNode = refUsers.document(auth.currentUser?.uid!!)
        // * get ref to saved comment
        val refCommentPost =
            refComment.document(idPost).collection(NAME_REF_LIST_COMMENTS).document(newComment.id)

        database.runTransaction { transition ->
            // * add comment and update field comments to post
            transition.set(refCommentPost, newComment)
            transition.update(currentPost, FIELD_NUMBER_COMMENTS, increment)
            val refComment = refCommentPost.path + newComment.id
            transition.update(userNode,
                "listRefComments",
                FieldValue.arrayUnion(refComment))
        }.await()
        return newComment.id
    }

    suspend fun addNewComment2(post: Post,newComment: String):String{
        val response=functions.getHttpsCallable("notifyComment2").call(
            mapOf(
                "idPost" to post.id,
                "comment" to newComment,
                "urlImgPost" to post.urlImage,
                "idPostOwner" to post.userPoster?.idUser.toString()
            )
        ).continueWith {task->
            val reponse=task.result.data as (Map<String, Object>)
            reponse["idComment"] as String
        }.await()

        Timber.d("response ${response}")
       return  response
    }
}