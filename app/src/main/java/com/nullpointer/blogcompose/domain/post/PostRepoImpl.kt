package com.nullpointer.blogcompose.domain.post

import com.nullpointer.blogcompose.data.remote.PostDataSource
import com.nullpointer.blogcompose.models.Post

class PostRepoImpl(
    private val postDataSource: PostDataSource,
) : PostRepository {
    override suspend fun getLastPost(nPost: Long): List<Post> =
        postDataSource.getLatestPost(nPost)

    override suspend fun getLastPostByUser(idUser: String, nPost: Long): List<Post> =
        postDataSource.getLatestPostFrom(idUser, nPost)

    override suspend fun getMyLastPost(nPost: Long): List<Post> =
        postDataSource.getMyLastPost(nPost)

    override suspend fun addNewPost(post: Post) =
        postDataSource.addNewPost(post)

    override suspend fun deleterPost(post: Post) =
        postDataSource.deleterPost(post)

    override suspend fun updatePost(post: Post) =
        postDataSource.update(post)
}