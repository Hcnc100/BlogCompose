package com.nullpointer.blogcompose.domain.post

import android.icu.number.IntegerWidth
import com.nullpointer.blogcompose.models.Post

interface PostRepository {
    suspend fun getLastPost(nPost: Int = Integer.MAX_VALUE): List<Post>
    suspend fun getLastPostByUser(idUser: String, nPost: Int = Integer.MAX_VALUE): List<Post>
    suspend fun getMyLastPost(nPost: Int = Integer.MAX_VALUE): List<Post>
    suspend fun addNewPost(post: Post)
    suspend fun deleterPost(post: Post)
    suspend fun updatePost(post: Post)
    suspend fun getPost(idPost: String): Post?
    suspend fun updateLikePost(post: Post, isLiked: Boolean): Post?
}