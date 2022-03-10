package com.nullpointer.blogcompose.domain.post

import com.nullpointer.blogcompose.core.utils.InternetCheck
import com.nullpointer.blogcompose.core.utils.NetworkException
import com.nullpointer.blogcompose.data.local.cache.MyPostDAO
import com.nullpointer.blogcompose.data.local.cache.PostDAO
import com.nullpointer.blogcompose.data.remote.PostDataSource
import com.nullpointer.blogcompose.models.MyPost
import com.nullpointer.blogcompose.models.Post
import kotlinx.coroutines.flow.Flow

class PostRepoImpl(
    private val postDataSource: PostDataSource,
    private val postDAO: PostDAO,
    private val myPostDAO: MyPostDAO,
) : PostRepository {

    companion object {
        private const val SIZE_POST_REQUEST = 5
    }

    suspend fun requestLastPost(): Int {
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

    suspend fun requestMyLastPost(): Int {
        if (!InternetCheck.isNetworkAvailable()) throw NetworkException()
        postDataSource.getLatestPost(SIZE_POST_REQUEST, beforeId = myPostDAO.getFirstPost()?.id)
            .also { list ->
                if (list.isNotEmpty()) {
                    myPostDAO.deleterAll()
                    val listSimplePost = list.map { MyPost.fromPost(it) }
                    myPostDAO.insertListPost(listSimplePost)
                }
                return list.size
            }
    }

    suspend fun concatenatePost(): Int {
        if (!InternetCheck.isNetworkAvailable()) throw NetworkException()
        postDataSource.getLatestPost(SIZE_POST_REQUEST, afterId = postDAO.getLastPost()?.id).also {
            postDAO.insertListPost(it)
            return it.size
        }
    }

    suspend fun concatenateMyPost(): Int {
        if (!InternetCheck.isNetworkAvailable()) throw NetworkException()
        postDataSource.getLatestPost(SIZE_POST_REQUEST, afterId = myPostDAO.getLastPost()?.id)
            .also { list ->
                val listSimplePost = list.map { MyPost.fromPost((it)) }
                myPostDAO.insertListPost(listSimplePost)
                return listSimplePost.size
            }
    }


    override fun getLastPost(inCaching: Boolean): Flow<List<Post>> =
        postDAO.getAllPost()


    override suspend fun getLastPostByUser(idUser: String, inCaching: Boolean): List<Post> {
        TODO("Not yet implemented")
    }

    override suspend fun getMyLastPost(inCaching: Boolean): Flow<List<Post>> =
        myPostDAO.getAllPost()


    override suspend fun updateLikePost(idPost: String, isLiked: Boolean) {
        try {
            // * if has null update post or dont have internet, launch exception
            if (!InternetCheck.isNetworkAvailable()) throw NetworkException()
            val postUpdate = postDataSource.updateLikes(idPost, isLiked)!!
            // * update the info from post
            if (postDAO.isPostExist(idPost)) postDAO.updatePost(postUpdate)
            if (myPostDAO.isPostExist(idPost)) myPostDAO.updatePost(MyPost.fromPost(postUpdate))
        } catch (e: Exception) {
            // ? if has problem restore data post
            postDAO.getPostById(idPost)?.let {
                postDAO.updatePost(it)
            }
            myPostDAO.getPostById(idPost)?.let {
                myPostDAO.updatePost(MyPost.fromPost(it))
            }
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