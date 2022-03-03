package com.nullpointer.blogcompose.data.remote

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.nullpointer.blogcompose.models.Post
import kotlinx.coroutines.tasks.await
import timber.log.Timber

class PostDataSource {
    private val refPosts = Firebase.firestore.collection("posts")
    private val refLikePost = Firebase.firestore.collection("likePost")
    private val database = Firebase.firestore
    private val auth = Firebase.auth

    suspend fun getLatestPost(nPosts: Int): List<Post> {
        return refPosts.orderBy(
            "timeStamp", Query.Direction.DESCENDING)
            .limit(nPosts.toLong()).get().await().documents.mapNotNull {
                transformDocumentPost(it)
            }
    }

    suspend fun getMyLastPost(nPosts: Int) =
        getLatestPostFrom(auth.currentUser?.uid!!, nPosts)

    suspend fun getLatestPostFrom(idUser: String, nPost: Int): List<Post> {
        return refPosts.orderBy("timeStamp", Query.Direction.DESCENDING)
            .whereEqualTo("postOwnerId", idUser).limit(nPost.toLong()).get()
            .await().documents.mapNotNull {
                transformDocumentPost(it)
            }
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
        val uuid = auth.currentUser?.uid
        val refLikePost = refLikePost.document(idPost).get().await()
        if (!refLikePost.exists()) return false
        val listIdLikes = refLikePost.get("likes") as List<String>
        return listIdLikes.contains(uuid)
    }

    suspend fun updateLikes(idPost: String, isLiked: Boolean): Post? {
        val increment = FieldValue.increment(1)
        val decrement = FieldValue.increment(-1)

        val currentPost = refPosts.document(idPost)
        val likePost = refLikePost.document(idPost)
        val uuid = auth.currentUser?.uid ?: ""
        var isSuccess = true

        database.runTransaction { transition ->
            val postSnapshot = transition.get(currentPost)
            val likesCount = postSnapshot.getLong("numberLikes")
            if (likesCount != null) {
                if (isLiked) {
                    if (transition.get(likePost).exists()) {
                        transition.update(likePost, "likes", FieldValue.arrayUnion(uuid))
                    } else {
                        transition.set(likePost,
                            hashMapOf("likes" to arrayListOf(uuid)),
                            SetOptions.merge())
                    }
                    transition.update(currentPost, "numberLikes", increment)
                } else {
                    transition.update(currentPost, "numberLikes", decrement)
                    transition.update(likePost, "likes", FieldValue.arrayRemove(uuid))
                }
            }else{
                isSuccess=false
            }
        }.addOnFailureListener {
            isSuccess = false
        }.await()

        return if (isSuccess) transformDocumentPost(currentPost.get().await()) else null
    }

    suspend fun getPost(idPost: String): Post? {
        val document = refPosts.document(idPost).get().await()
        return document.toObject(Post::class.java)?.apply {
            timeStamp = document.getTimestamp(
                "timeStamp",
                DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
            )?.toDate()
//            ownerLike = listLikes.contains(auth.currentUser?.uid!!)
            id = document.id
        }
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