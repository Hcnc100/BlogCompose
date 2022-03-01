package com.nullpointer.blogcompose.domain.post

import com.nullpointer.blogcompose.models.Post

interface PostRepository {
    suspend fun getLastPost(nPost: Int): List<Post>
    suspend fun getLastPostByUser(idUser: String, nPost: Int): List<Post>
    suspend fun getMyLastPost( nPost: Int): List<Post>
    suspend fun addNewPost(post: Post)
    suspend fun deleterPost(post: Post)
    suspend fun updatePost(post: Post)
}