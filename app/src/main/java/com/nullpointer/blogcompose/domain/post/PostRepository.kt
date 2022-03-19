package com.nullpointer.blogcompose.domain.post

import android.content.Context
import com.nullpointer.blogcompose.models.Comment
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
    suspend fun addNewPost(post: Post, context: Context)
    suspend fun deleterPost(post: Post)
    suspend fun updateInnerPost(post: Post)
    suspend fun getPost(idPost: String): Post?
    suspend fun updateLikePost(idPost: String, isLiked: Boolean? = null)
    suspend fun deleterAllPost()
    suspend fun addNewComment(idPost: String,comment: String)
    fun getRealTimePost(idPost: String): Flow<Post?>
    fun getCommetsRealTime(idPost: String): Flow<List<Comment>>
}