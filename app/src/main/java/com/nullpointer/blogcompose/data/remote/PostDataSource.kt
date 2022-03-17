package com.nullpointer.blogcompose.data.remote

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.nullpointer.blogcompose.core.utils.InternetCheck
import com.nullpointer.blogcompose.models.Post
import kotlinx.coroutines.tasks.await

class PostDataSource {
    private val database = Firebase.firestore
    private val refPosts = database.collection("posts")
    private val refLikePost = database.collection("likePost")
    private val auth = Firebase.auth

    private suspend fun getLastPost(
        nPosts: Int = Int.MAX_VALUE,
        startWithPostId: String? = null,
        endWithPostId: String? = null,
        fromUserId: String? = null,
        includePost: Boolean = false,
    ): List<Post> {
        // * base query
        var query = refPosts.orderBy("timeStamp", Query.Direction.DESCENDING)
        // * filter to user or for default get all
        if (fromUserId != null) query = query.whereEqualTo("poster.uuid", fromUserId)
        // * get documents after that
        if (startWithPostId != null) {
            val refDocument = refPosts.document(startWithPostId).get().await()
            if (refDocument.exists())
                query =
                    if (includePost) query.startAt(refDocument) else query.startAfter(refDocument)
        }
        if (endWithPostId != null) {
            val refDocument = refPosts.document(endWithPostId).get().await()
            if (refDocument.exists())
                query = if (includePost) query.endAt(refDocument) else query.endBefore(refDocument)
        }
        // * limit result or for default all
        if (nPosts != Integer.MAX_VALUE) query = query.limit(nPosts.toLong())
        return query.get(Source.SERVER).await().documents.mapNotNull {
            transformDocumentPost(it)
        }
    }

    suspend fun getLatestPost(
        nPosts: Int = Integer.MAX_VALUE,
        startWithPostId: String? = null,
        endWithPostId: String? = null,
        includePost: Boolean = false,
    ): List<Post> {
        return getLastPost(
            nPosts = nPosts,
            startWithPostId = startWithPostId,
            endWithPostId = endWithPostId,
            includePost = includePost)
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

    suspend fun getLastPostByUser(idUser: String, nPost: Int = Integer.MAX_VALUE): List<Post> {
        return getLastPost(nPost, idUser)
    }

    suspend fun getPost(idPost: String): Post? {
        val document = refPosts.document(idPost).get().await()
        return transformDocumentPost(document)
    }


    private suspend fun transformDocumentPost(documentSnapshot: DocumentSnapshot): Post? {
        return documentSnapshot.toObject(Post::class.java)?.let { post ->
            post.apply {
                timeStamp = documentSnapshot.getTimestamp(
                    "timeStamp",
                    DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                )?.toDate()
                ownerLike = isPostLiked(documentSnapshot.id)
                id = documentSnapshot.id
            }
        }
    }

    private suspend fun isPostLiked(idPost: String): Boolean {
        return if (InternetCheck.isNetworkAvailable()) {
            val uuid = auth.currentUser?.uid!!
            val refLikePost = refLikePost.document(idPost).collection("usersLike").document(uuid)
            refLikePost.get().await().exists()
        } else {
            false
        }
    }

    suspend fun updateLikes(idPost: String, isLiked: Boolean): Post? {
        val increment = FieldValue.increment(1)
        val decrement = FieldValue.increment(-1)

        val currentPost = refPosts.document(idPost)
        val uuid = auth.currentUser?.uid ?: ""
        var isSuccess = true
        val refLikePost = refLikePost.document(idPost).collection("usersLike").document(uuid)

        database.runTransaction { transition ->
            val postSnapshot = transition.get(currentPost)
            val likesCount = postSnapshot.getLong("numberLikes")
            if (likesCount != null) {
                if (isLiked) {
                    transition.set(refLikePost, mapOf("timestamp" to FieldValue.serverTimestamp()))
                    transition.update(currentPost, "numberLikes", increment)
                } else {
                    transition.update(currentPost, "numberLikes", decrement)
                    transition.delete(refLikePost)
                }
            } else {
                isSuccess = false
            }
        }.addOnFailureListener {
            isSuccess = false
        }.await()

        return if (isSuccess) transformDocumentPost(currentPost.get().await()) else null
    }

    suspend fun addNewPost(post: Post) {
        refPosts.document(post.id).set(post).await()
    }

    suspend fun deleterPost(post: Post) {
        refPosts.document(post.id).delete().await()
    }

    suspend fun update(post: Post) {
        refPosts.document(post.id).set(post).await()
    }
}