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

    override val listLastPost: Flow<List<Post>> = postDAO.getAllPost()
    override val listMyLastPost: Flow<List<MyPost>> = myPostDAO.getAllPost()

    override suspend fun requestLastPost(forceRefresh: Boolean): Int {
        if (!InternetCheck.isNetworkAvailable()) throw NetworkException()
        // * get last post consideration first post saved in database
        val idFirstPost = if (forceRefresh) null else postDAO.getFirstPost()?.id
        val listLastPost = postDataSource.getLatestPost(
            nPosts = SIZE_POST_REQUEST,
            beforeId = idFirstPost
        )
        if (listLastPost.isNotEmpty()) postDAO.updateAllPost(listLastPost)
        return listLastPost.size
    }

    override suspend fun requestMyLastPost(forceRefresh: Boolean): Int {
        if (!InternetCheck.isNetworkAvailable()) throw NetworkException()
        // * get last post consideration "my" first post saved in database
        // ? and remove repeater info because owner is me
        val idMyFirstPost = if (forceRefresh) null else myPostDAO.getFirstPost()?.id
        val listMyLastPost = postDataSource.getMyLastPost(
            nPosts = SIZE_POST_REQUEST,
            beforeId = idMyFirstPost
        ).map { MyPost.fromPost(it) }
        if (listMyLastPost.isNotEmpty()) myPostDAO.updateAllPost(listMyLastPost)
        return listMyLastPost.size
    }

    override suspend fun concatenatePost(): Int {
        // * get post and concatenate to all post
        // * this nos remove old post
        // * consideration first post saved
        if (!InternetCheck.isNetworkAvailable()) throw NetworkException()
        val listNewPost = postDataSource.getLatestPost(
            nPosts = SIZE_POST_REQUEST,
            afterId = postDAO.getLastPost()?.id
        )
        postDAO.insertListPost(listNewPost)
        return listNewPost.size
    }

    override suspend fun concatenateMyPost(): Int {
        // * get "my post" and concatenate
        // * this nos remove old post
        // * consideration first post saved
        if (!InternetCheck.isNetworkAvailable()) throw NetworkException()
        val listMyNewPost = postDataSource.getMyLastPost(
            nPosts = SIZE_POST_REQUEST,
            afterId = myPostDAO.getLastPost()?.id
        )
        val listSimplePost = listMyNewPost.map { MyPost.fromPost(it) }
        myPostDAO.insertListPost(listSimplePost)
        return listSimplePost.size
    }

    override suspend fun getLastPostByUser(idUser: String, inCaching: Boolean): List<Post> {
        TODO("Not yet implemented")
    }


    override suspend fun updateLikePost(idPost: String, isLiked: Boolean) {
        val oldPost = postDAO.getPostById(idPost)
        val oldMyPost = myPostDAO.getPostById(idPost)
        try {
            // * update fake post
            if (oldPost != null) postDAO.updatePost(oldPost.copyInnerLike(isLiked))
            if (oldMyPost != null) myPostDAO.updatePost(oldMyPost.copyInnerLike(isLiked))

            // * if has null update post or dont have internet, launch exception
            if (!InternetCheck.isNetworkAvailable()) throw NetworkException()
            val postUpdate = postDataSource.updateLikes(idPost, isLiked)!!

            // * update the info from post
            if (oldPost != null) postDAO.updatePost(postUpdate)
            if (oldMyPost != null) myPostDAO.updatePost(MyPost.fromPost(postUpdate))
        } catch (e: Exception) {
            // ? if has problem restore data post
            oldPost?.let { postDAO.updatePost(oldPost) }
            oldMyPost?.let { myPostDAO.updatePost(it) }

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