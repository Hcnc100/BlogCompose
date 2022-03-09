package com.nullpointer.blogcompose.domain.post

import com.nullpointer.blogcompose.models.Post
import kotlinx.coroutines.flow.Flow

interface PostRepository {
     fun getLastPost(inCaching:Boolean): Flow<List<Post>>
    suspend fun getLastPostByUser(idUser: String, inCaching:Boolean): List<Post>
    suspend fun getMyLastPost(idUser: String, inCaching:Boolean): List<Post>
    suspend fun addNewPost(post: Post)
    suspend fun deleterPost(post: Post)
    suspend fun updatePost(post: Post)
    suspend fun getPost(idPost: String): Post?
    suspend fun updateLikePost(post: Post, isLiked: Boolean): Post?
}