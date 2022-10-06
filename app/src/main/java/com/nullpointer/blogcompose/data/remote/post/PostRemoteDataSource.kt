package com.nullpointer.blogcompose.data.remote.post

import com.nullpointer.blogcompose.models.notify.Notify
import com.nullpointer.blogcompose.models.posts.Post
import kotlinx.coroutines.flow.Flow
import java.util.*

interface PostRemoteDataSource {

    fun getRealTimePost(idPost: String): Flow<Post?>

    suspend fun addNewPost(post: Post): String
    suspend fun deleterPost(idPost: String)
    suspend fun getPost(idPost: String): Post?


    suspend fun getLastPost(
        nPosts: Int = Int.MAX_VALUE,
        startWithPostId: String? = null,
        endWithPostId: String? = null,
        fromUserId: String? = null,
        includePost: Boolean = false,
    ): List<Post>

    suspend fun getLastPostBeforeThat(
        date: Date? = null,
        nPosts: Int = Integer.MAX_VALUE,
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