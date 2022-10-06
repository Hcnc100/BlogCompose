package com.nullpointer.blogcompose.data.local.post

import com.nullpointer.blogcompose.models.posts.MyPost
import com.nullpointer.blogcompose.models.posts.Post
import com.nullpointer.blogcompose.models.posts.SimplePost
import kotlinx.coroutines.flow.Flow

interface PostLocalDataSource {
    val listLastPost: Flow<List<Post>>
    val listMyLastPost: Flow<List<MyPost>>

    suspend fun getLastMyPost(): MyPost?
    suspend fun getLastPost(): Post?
    suspend fun updateAllPost(listPost: List<Post>)
    suspend fun updateAllMyPost(listMyPost: List<MyPost>)
    suspend fun insertListPost(listPost: List<Post>)
    suspend fun insertListMyPost(listMyPost: List<MyPost>)
    suspend fun getFirstPost(): Post?
    suspend fun getMyFirstPost(): MyPost?
    suspend fun updatePost(post: SimplePost)
    suspend fun deleterAllPost()
}