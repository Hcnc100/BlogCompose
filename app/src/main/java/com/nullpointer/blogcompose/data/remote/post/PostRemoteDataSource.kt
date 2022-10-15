package com.nullpointer.blogcompose.data.remote.post

import com.nullpointer.blogcompose.models.notify.Notify
import com.nullpointer.blogcompose.models.posts.Post
import kotlinx.coroutines.flow.Flow

interface PostRemoteDataSource {

    fun getRealTimePost(idPost: String): Flow<Post?>

    suspend fun addNewPost(post: Post): String
    suspend fun deleterPost(idPost: String)
    suspend fun getPost(idPost: String): Post?


    suspend fun getLastPostBetween(
        endWithId: String?,
        fromUserId: String?,
        startWithId: String
    ): List<Post>

    suspend fun getLastPost(
        idPost: String? = null,
        fromUserId: String? = null,
        includePost: Boolean = false,
        numberPost: Long = Long.MAX_VALUE
    ): List<Post>

    suspend fun getConcatenatePost(
        idPost: String? = null,
        fromUserId: String? = null,
        numberPosts: Long = Long.MAX_VALUE,
    ): List<Post>

    suspend fun updateLikes(
        idUser: String,
        idPost: String,
        notify: Notify?,
        isLiked: Boolean,
        ownerPost: String
    ): Post?
}