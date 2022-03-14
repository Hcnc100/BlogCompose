package com.nullpointer.blogcompose.domain.post

import com.nullpointer.blogcompose.models.MyPost
import com.nullpointer.blogcompose.models.Post
import kotlinx.coroutines.flow.Flow

interface PostRepository {
    val listLastPost: Flow<List<Post>>
    val listMyLastPost: Flow<List<MyPost>>
    suspend fun getLastPostByUser(idUser: String, inCaching: Boolean): List<Post>
    suspend fun requestLastPost(forceRefresh: Boolean = false): Int
    suspend fun requestMyLastPost(forceRefresh: Boolean = false): Int
    suspend fun concatenatePost(): Int
    suspend fun concatenateMyPost(): Int
    suspend fun addNewPost(post: Post)
    suspend fun deleterPost(post: Post)
    suspend fun updatePost(post: Post)
    suspend fun getPost(idPost: String): Post?
    suspend fun updateLikePost(idPost: String, isLiked: Boolean)
    suspend fun deleterAllPost()
}