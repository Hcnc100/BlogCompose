package com.nullpointer.blogcompose.domain.post

import com.nullpointer.blogcompose.models.posts.MyPost
import com.nullpointer.blogcompose.models.posts.Post
import kotlinx.coroutines.flow.Flow

interface PostRepository {
    val listLastPost: Flow<List<Post>>
    val listMyLastPost: Flow<List<MyPost>>
    suspend fun requestLastPost(forceRefresh: Boolean = false): Int
    suspend fun requestMyLastPost(forceRefresh: Boolean = false): Int
    suspend fun concatenatePost(): Int
    suspend fun concatenateMyPost(): Int
    suspend fun addNewPost(post: Post)
    suspend fun updateLikePost(post: Post, isLiked: Boolean)
    suspend fun updatePostById(idPost: String)
    suspend fun updatePost(post: Post)
    suspend fun getRealTimePost(idPost: String): Flow<Post?>
}