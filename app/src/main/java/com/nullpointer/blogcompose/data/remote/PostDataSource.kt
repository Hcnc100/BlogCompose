package com.nullpointer.blogcompose.data.remote

import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.nullpointer.blogcompose.models.Post
import kotlinx.coroutines.tasks.await

class PostDataSource {
    private val refPosts = Firebase.firestore.collection("posts")
    private val auth=Firebase.auth

    suspend fun getLatestPost(nPosts: Int): List<Post> {
        return refPosts.orderBy("timeStamp", Query.Direction.DESCENDING)
            .limit(nPosts.toLong()).get().await().documents.mapNotNull { document ->
                document.toObject(Post::class.java)?.let { post ->
                    post.apply {
                        timeStamp = document.getTimestamp(
                            "timeStamp",
                            DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                        )?.toDate()
                        id = document.id
                    }
                }
            }
    }

    suspend fun getMyLastPost(nPosts: Int) =
        getLatestPostFrom(auth.currentUser?.uid!!,nPosts)

    suspend fun getLatestPostFrom(idUser: String, nPost: Int): List<Post> {
        return refPosts.orderBy("timeStamp", Query.Direction.DESCENDING)
            .whereEqualTo("postOwnerId", idUser).limit(nPost.toLong()).get()
            .await().documents.mapNotNull { document ->
                document.toObject(Post::class.java)?.let { post ->
                    post.apply {
                        timeStamp = document.getTimestamp(
                            "timeStamp",
                            DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                        )?.toDate()
                        id = document.id
                    }
                }
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