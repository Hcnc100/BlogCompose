package com.nullpointer.blogcompose.domain.post

import com.nullpointer.blogcompose.core.utils.InternetCheck
import com.nullpointer.blogcompose.core.utils.NetworkException
import com.nullpointer.blogcompose.data.local.cache.MyPostDAO
import com.nullpointer.blogcompose.data.local.cache.PostDAO
import com.nullpointer.blogcompose.data.local.preferences.PreferencesDataSource
import com.nullpointer.blogcompose.data.remote.post.PostDataSource
import com.nullpointer.blogcompose.models.notify.Notify
import com.nullpointer.blogcompose.models.notify.TypeNotify
import com.nullpointer.blogcompose.models.posts.MyPost
import com.nullpointer.blogcompose.models.posts.Post
import com.nullpointer.blogcompose.models.posts.SimplePost
import com.nullpointer.blogcompose.models.users.MyUser
import kotlinx.coroutines.flow.Flow
import java.util.*

class PostRepoImpl(
    private val postDataSource: PostDataSource,
    private val prefDataSource: PreferencesDataSource,
    private val postDAO: PostDAO,
    private val myPostDAO: MyPostDAO,
) : PostRepository {

    companion object {
        private const val SIZE_POST_REQUEST = 5
        private const val SIZE_MY_POST_REQUEST = 10
    }

    override val listLastPost: Flow<List<Post>> = postDAO.getAllPost()
    override val listMyLastPost: Flow<List<MyPost>> = myPostDAO.getAllPost()


    override suspend fun requestLastPost(forceRefresh: Boolean): Int {
        val firstPost = if (forceRefresh) null else postDAO.getFirstPost()
        val listLastPost = getPostBeforeThat(
            firstPost?.timestamp,
            size = SIZE_POST_REQUEST
        )
        if (listLastPost.isNotEmpty()) postDAO.updateAllPost(listLastPost)
        return listLastPost.size
    }


    override suspend fun requestMyLastPost(forceRefresh: Boolean): Int {
        val firstPost = if (forceRefresh) null else myPostDAO.getFirstPost()
        val listMyLastPost = getPostBeforeThat(
            beforePostTimestamp = firstPost?.timestamp,
            fromUser = prefDataSource.getIdUser(),
            size = SIZE_MY_POST_REQUEST
        ).map { MyPost.fromPost(it) }
        if (listMyLastPost.isNotEmpty()) myPostDAO.updateAllPost(listMyLastPost)
        return listMyLastPost.size
    }

    override suspend fun concatenatePost(): Int {
        postDAO.getLastPost()?.let { lastPost ->
            val concatenatePost = getPostStartWith(
                postStartId = lastPost.id,
                size = SIZE_POST_REQUEST
            )
            postDAO.insertListPost(concatenatePost)
            return concatenatePost.size
        }
        return 0
    }

    override suspend fun concatenateMyPost(): Int {
        myPostDAO.getLastPost()?.let { lastPost ->
            val concatenatePost = getPostStartWith(
                fromUser = prefDataSource.getIdUser(),
                postStartId = lastPost.id,
                size = SIZE_MY_POST_REQUEST
            )
            val listSimplePost = concatenatePost.map { MyPost.fromPost(it) }
            myPostDAO.insertListPost(listSimplePost)
            return listSimplePost.size
        }
        return 0
    }


    override suspend fun updatePost(idPost: String) {
        updateInnerPost(idPost) { _, _ ->
            postDataSource.getPost(idPost)
        }
    }

    override suspend fun updatePost(post: Post) {
        updateInnerPost(post.id) { _, _ -> post }
    }


    override suspend fun updateLikePost(post: SimplePost, isLiked: Boolean) {
        updateInnerPost(post.id) { oldPost, oldMyPost ->

            val currentUser = prefDataSource.getCurrentUser()
            val newNotify = if (post.userPoster?.idUser != prefDataSource.getIdUser() && isLiked)
                post.createLikeNotify(currentUser) else null

            // * update fake post
            if (oldPost != null) postDAO.updatePost(oldPost.toggleLike())
            if (oldMyPost != null) myPostDAO.updatePost(oldMyPost.toggleLike())

            postDataSource.updateLikes(
                idPost = post.id,
                isLiked = !post.ownerLike,
                notify = newNotify,
                ownerPost = post.userPoster?.idUser!!,
                idUser = currentUser.idUser
            )
        }
    }

    override suspend fun requestLastPostInitWith(idPost: String) {
        getPostStartWith(
            postStartId = idPost,
            includePost = true,
            size = SIZE_POST_REQUEST
        ).let { listPost ->
            if (listPost.isNotEmpty()) {
                postDAO.updateAllPost(listPost)
                myPostDAO.updateAllPost(listPost.map { MyPost.fromPost(it) })
            }
        }
    }

    override suspend fun deleterAllPost() {
        postDAO.deleterAll()
        myPostDAO.deleterAll()
    }

    override suspend fun getRealTimePost(idPost: String): Flow<Post?> {
        if (!InternetCheck.isNetworkAvailable()) throw NetworkException()
        return postDataSource.getRealTimePost(idPost)
    }


    override suspend fun addNewPost(post: Post) {
        val idPost = postDataSource.addNewPost(post)
        requestLastPostInitWith(idPost)
    }


    private suspend fun getPostBeforeThat(
        beforePostTimestamp: Date?,
        fromUser: String? = null,
        size: Int
    ): List<Post> {
        if (!InternetCheck.isNetworkAvailable()) throw NetworkException()
        return postDataSource.getLastPostBeforeThat(
            date = beforePostTimestamp,
            nPosts = size,
            fromUserId = fromUser
        )
    }

    private suspend fun getPostStartWith(
        fromUser: String? = null,
        postStartId: String,
        includePost: Boolean = false,
        size: Int
    ): List<Post> {
        if (!InternetCheck.isNetworkAvailable()) throw NetworkException()
        return postDataSource.getLastPost(
            nPosts = size,
            startWithPostId = postStartId,
            fromUserId = fromUser,
            includePost = includePost
        )
    }

    override suspend fun deleterPost(idPost: String) {
        postDAO.deleterPost(idPost)
        myPostDAO.deleterPost(idPost)
    }


    private suspend fun updateInnerPost(
        idPost: String,
        requestUpdatePost: suspend (oldPostCopy: Post?, oldMyPostCopy: MyPost?) -> Post?
    ) {
        if (!InternetCheck.isNetworkAvailable()) throw NetworkException()
        val oldPost = postDAO.getPostById(idPost)
        val oldMyPost = myPostDAO.getPostById(idPost)
        try {
            val postUpdate = requestUpdatePost(oldPost?.copy(), oldMyPost?.copy())!!
            if (oldPost != null) postDAO.updatePost(postUpdate)
            if (oldMyPost != null) myPostDAO.updatePost(MyPost.fromPost(postUpdate))
        } catch (e: Exception) {
            if (oldPost != null) postDAO.updatePost(oldPost)
            if (oldMyPost != null) myPostDAO.updatePost(oldMyPost)
            throw e
        }
    }

    private fun SimplePost.createLikeNotify(myUser: MyUser): Notify {
        return Notify(
            userInNotify = myUser.toInnerUser(),
            idPost = id,
            urlImgPost = urlImage,
            type = TypeNotify.LIKE
        )
    }

}