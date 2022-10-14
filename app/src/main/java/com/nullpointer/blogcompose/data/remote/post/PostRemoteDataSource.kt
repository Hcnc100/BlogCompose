package com.nullpointer.blogcompose.data.remote.post

import com.nullpointer.blogcompose.models.notify.Notify
import com.nullpointer.blogcompose.models.posts.Post
import kotlinx.coroutines.flow.Flow

interface PostRemoteDataSource {

    fun getRealTimePost(idPost: String): Flow<Post?>

    suspend fun addNewPost(post: Post): String
    suspend fun deleterPost(idPost: String)
    suspend fun getPost(idPost: String): Post?


    suspend fun getLastPost(
        numberPost: Long = Long.MAX_VALUE,
        idPost: String? = null,
        fromUserId: String? = null,
        includePost: Boolean = false
    ): List<Post>

    suspend fun getConcatenatePost(
        idPost: String? = null,
        numberPosts: Long = Long.MAX_VALUE,
        fromUserId: String? = null,
    ): List<Post>

    suspend fun updateLikes(
        idPost: String,
        isLiked: Boolean,
        notify: Notify?,
        ownerPost: String,
        idUser: String
    ): Post?
}