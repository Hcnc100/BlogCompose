package com.nullpointer.blogcompose.data.remote.post

import com.nullpointer.blogcompose.models.posts.Post

interface PostDataSource {

    suspend fun addNewPost(post: Post): String
    suspend fun getPost(idPost: String): Post?
    suspend fun getLastPost(
        nPosts: Int = Int.MAX_VALUE,
        startWithPostId: String? = null,
        endWithPostId: String? = null,
        fromUserId: String? = null,
        includePost: Boolean = false,
    ): List<Post>
}