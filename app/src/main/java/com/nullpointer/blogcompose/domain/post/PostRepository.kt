package com.nullpointer.blogcompose.domain.post

import com.nullpointer.blogcompose.models.posts.MyPost
import com.nullpointer.blogcompose.models.posts.Post
import com.nullpointer.blogcompose.models.posts.SimplePost
import kotlinx.coroutines.flow.Flow

interface PostRepository {
    val listLastPost: Flow<List<Post>>
    val listMyLastPost: Flow<List<MyPost>>
    suspend fun requestLastPost(forceRefresh: Boolean = false): Int
    suspend fun requestMyLastPost(forceRefresh: Boolean = false): Int
    suspend fun requestLastPostInitWith(idPost: String)
    suspend fun concatenatePost(): Int
    suspend fun concatenateMyPost(): Int
    suspend fun addNewPost(post: Post)
    suspend fun deleterPost(idPost: String)
    suspend fun updatePost(post: Post)
    suspend fun updateLikePost(post: SimplePost,isLiked: Boolean)
    suspend fun updatePost(idPost: String)
    suspend fun deleterAllPost()
    suspend fun getRealTimePost(idPost: String): Flow<Post?>
}