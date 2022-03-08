package com.nullpointer.blogcompose.data.remote

import com.google.android.gms.tasks.Tasks.await
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.ktx.Firebase
import com.nullpointer.blogcompose.core.utils.InternetCheck
import com.nullpointer.blogcompose.models.Post
import kotlinx.coroutines.tasks.await
import timber.log.Timber

class PostDataSource {
    private val database = Firebase.firestore
    private val refPosts = database.collection("posts")
    private val refLikePost = database.collection("likePost")
    private val auth = Firebase.auth

    private suspend fun getLastPost(
        reference: CollectionReference,
        nPosts: Int = Int.MAX_VALUE,
        fromUserId: String? = null,
        source: Source? = null,
    ): List<Post> {
        // * base query
        var query = reference.orderBy("timeStamp", Query.Direction.DESCENDING)
        // * filter to user or for default get all
        if (fromUserId != null) query = query.whereEqualTo("postOwnerId", fromUserId)
        // * limit result or for default all
        if (nPosts != Integer.MAX_VALUE) query = query.limit(nPosts.toLong())

        return query.get().await().documents.mapNotNull { transformDocumentPost(it) }
    }

    suspend fun getLatestPost(
        nPosts: Int = Integer.MAX_VALUE,
    ): List<Post> {
        return getLastPost(refPosts, nPosts)
    }

    suspend fun getMyLastPost(
        nPosts: Int = Integer.MAX_VALUE,
    ): List<Post> {
        return getLastPost(refPosts, nPosts, auth.currentUser?.uid!!)
    }

    suspend fun getLastPostByUser(idUser: String, nPost: Int=Integer.MAX_VALUE): List<Post> {
        return getLastPost(refPosts, nPost, idUser)
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

    suspend fun updateLikes(post: Post, isLiked: Boolean): Post? {
        if (!InternetCheck.isNetworkAvailable()) return null
        val increment = FieldValue.increment(1)
        val decrement = FieldValue.increment(-1)

        val currentPost = refPosts.document(post.id)
        val uuid = auth.currentUser?.uid ?: ""
        var isSuccess = true
        val refLikePost = refLikePost.document(post.id).collection("usersLike").document(uuid)

        database.runTransaction { transition ->
            val postSnapshot = transition.get(currentPost)
            val likesCount = postSnapshot.getLong("numberLikes")
            if (likesCount != null) {
//                if(!transition.get(likePost).exists()) {
//                    transition.set(likePost, mapOf("ownerPost" to post.poster!!.uuid))
//                }
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
        refPosts.add(post).await()
    }

    suspend fun deleterPost(post: Post) {
        refPosts.document(post.id).delete().await()
    }

    suspend fun update(post: Post) {
        refPosts.document(post.id).set(post).await()
    }
}