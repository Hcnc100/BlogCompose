package com.nullpointer.blogcompose.domain.post

import com.nullpointer.blogcompose.core.utils.InternetCheck
import com.nullpointer.blogcompose.core.utils.NetworkException
import com.nullpointer.blogcompose.data.local.cache.PostDAO
import com.nullpointer.blogcompose.data.remote.PostDataSource
import com.nullpointer.blogcompose.models.Post
import kotlinx.coroutines.flow.Flow
import java.lang.Exception

class PostRepoImpl(
    private val postDataSource: PostDataSource,
    private val postDAO: PostDAO,
) : PostRepository {

    companion object {
        private const val SIZE_CACHE = 5
        private const val SIZE_POST_REQUEST = 5
    }

    suspend fun requestNewPost(): Int {
        if (!InternetCheck.isNetworkAvailable()) throw NetworkException()
        postDataSource.getLatestPost(SIZE_POST_REQUEST, beforeId = postDAO.getFirstPost()?.id)
            .also {
                if (it.isNotEmpty()) {
                    postDAO.deleterAll()
                    postDAO.insertListPost(it)
                }
                return it.size
            }
    }

    suspend fun concatenatePost(): Int {
        if (!InternetCheck.isNetworkAvailable()) throw NetworkException()
        postDataSource.getLatestPost(SIZE_POST_REQUEST, afterId = postDAO.getLastPost()?.id).also {
            postDAO.insertListPost(it)
            return it.size
        }
    }


    override fun getLastPost(inCaching: Boolean): Flow<List<Post>> {
        return postDAO.getAllPost()
    }

    override suspend fun getLastPostByUser(idUser: String, inCaching: Boolean): List<Post> {
        return if (!inCaching && InternetCheck.isNetworkAvailable()) {
            postDataSource.getLastPostByUser(idUser)
        } else {
            postDAO.getPostByUser(idUser)
        }
    }

    override suspend fun getMyLastPost(idUser: String, inCaching: Boolean): List<Post> {
        return if (!inCaching && InternetCheck.isNetworkAvailable()) {
            postDataSource.getMyLastPost()
        } else {
            postDAO.getPostByUser(idUser)
        }
    }

    override suspend fun updateLikePost(oldPost: Post, isLiked: Boolean) {
        try {
            val postUpdate = postDataSource.updateLikes(oldPost, isLiked)!!
            postDAO.updatePost(postUpdate)
        } catch (e: Exception) {
            postDAO.updatePost(oldPost)
            throw e
        }
    }

    override suspend fun addNewPost(post: Post) =
        postDataSource.addNewPost(post)

    override suspend fun deleterPost(post: Post) =
        postDataSource.deleterPost(post)

    override suspend fun updatePost(post: Post) =
        postDataSource.update(post)

    override suspend fun getPost(idPost: String) =
        postDataSource.getPost(idPost)


}