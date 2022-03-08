package com.nullpointer.blogcompose.domain.post

import com.nullpointer.blogcompose.core.utils.InternetCheck
import com.nullpointer.blogcompose.data.local.cache.PostDAO
import com.nullpointer.blogcompose.data.remote.PostDataSource
import com.nullpointer.blogcompose.models.Post

class PostRepoImpl(
    private val postDataSource: PostDataSource,
    private val postDAO: PostDAO,
) : PostRepository {

    companion object {
        private const val SIZE_CACHE = 20
    }

    override suspend fun getLastPost(inCaching:Boolean): List<Post> {
        return if (!inCaching && InternetCheck.isNetworkAvailable()) {
            postDataSource.getLatestPost().also {
                val listSaved = if (it.size < SIZE_CACHE) it else it.subList(0, SIZE_CACHE)
                postDAO.deleterAll()
                postDAO.insertListPost(listSaved)
            }
        } else {
            postDAO.getAllPost()
        }
    }

    override suspend fun getLastPostByUser(idUser: String, inCaching:Boolean): List<Post> {
        return if(!inCaching && InternetCheck.isNetworkAvailable()){
            postDataSource.getLastPostByUser(idUser)
        }else{
            postDAO.getPostByUser(idUser)
        }
    }

    override suspend fun getMyLastPost(idUser: String, inCaching:Boolean): List<Post> {
        return if(!inCaching && InternetCheck.isNetworkAvailable()){
            postDataSource.getMyLastPost()
        }else{
            postDAO.getPostByUser(idUser)
        }
    }

    override suspend fun updateLikePost(post: Post, isLiked: Boolean): Post? {
        return postDataSource.updateLikes(post, isLiked)?.also {
            postDAO.updatePost(it)
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