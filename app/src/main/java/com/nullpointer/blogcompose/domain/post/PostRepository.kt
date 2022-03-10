package com.nullpointer.blogcompose.domain.post

import com.nullpointer.blogcompose.models.MyPost
import com.nullpointer.blogcompose.models.Post
import kotlinx.coroutines.flow.Flow

interface PostRepository {
     fun getLastPost(inCaching:Boolean): Flow<List<Post>>
    suspend fun getLastPostByUser(idUser: String, inCaching:Boolean): List<Post>
    suspend fun getMyLastPost(inCaching:Boolean): Flow<List<MyPost>>
    suspend fun addNewPost(post: Post)
    suspend fun deleterPost(post: Post)
    suspend fun updatePost(post: Post)
    suspend fun getPost(idPost: String): Post?
    suspend fun updateLikePost(idPost: String, isLiked: Boolean)
}