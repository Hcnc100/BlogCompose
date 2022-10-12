package com.nullpointer.blogcompose.domain.post

import com.nullpointer.blogcompose.core.utils.ExceptionManager.NO_INTERNET_CONNECTION
import com.nullpointer.blogcompose.core.utils.InternetCheck
import com.nullpointer.blogcompose.core.utils.NetworkException
import com.nullpointer.blogcompose.data.local.post.PostLocalDataSource
import com.nullpointer.blogcompose.data.local.preferences.PreferencesDataSource
import com.nullpointer.blogcompose.data.remote.post.PostRemoteDataSource
import com.nullpointer.blogcompose.models.notify.Notify
import com.nullpointer.blogcompose.models.notify.TypeNotify
import com.nullpointer.blogcompose.models.posts.MyPost
import com.nullpointer.blogcompose.models.posts.Post
import com.nullpointer.blogcompose.models.posts.SimplePost
import com.nullpointer.blogcompose.models.users.SimpleUser
import kotlinx.coroutines.flow.Flow
import java.util.*

@Suppress("UNCHECKED_CAST")
class PostRepoImpl(
    private val postDataSource: PostRemoteDataSource,
    private val prefDataSource: PreferencesDataSource,
    private val postLocalDataSource: PostLocalDataSource
) : PostRepository {

    companion object {
        private const val SIZE_POST_REQUEST = 5
        private const val SIZE_MY_POST_REQUEST = 7
    }

    override val listLastPost: Flow<List<Post>> = postLocalDataSource.listLastPost
    override val listMyLastPost: Flow<List<MyPost>> = postLocalDataSource.listMyLastPost


    override suspend fun requestLastPost(forceRefresh: Boolean): Int {
        val firstPost = if (forceRefresh) null else postLocalDataSource.getFirstPost()
        val listLastPost = getPostBeforeThat(
            firstPost?.timestamp,
            size = SIZE_POST_REQUEST
        )
        postLocalDataSource.updateAllPost(listLastPost as List<Post>)
        return listLastPost.size
    }


    override suspend fun requestMyLastPost(forceRefresh: Boolean): Int {
        val firstPost = if (forceRefresh) null else postLocalDataSource.getMyFirstPost()
        val listMyLastPost = getPostBeforeThat(
            beforePostTimestamp = firstPost?.timestamp,
            fromUser = prefDataSource.getIdUser(),
            size = SIZE_MY_POST_REQUEST
        )
        postLocalDataSource.updateAllMyPost(listMyLastPost as List<MyPost>)
        return listMyLastPost.size
    }

    override suspend fun concatenatePost(): Int {
        postLocalDataSource.getLastPost()?.let { lastPost ->
            val concatenatePost = getPostStartWith(
                postStartId = lastPost.id,
                size = SIZE_POST_REQUEST
            )
            postLocalDataSource.insertListPost(concatenatePost as List<Post>)
            return concatenatePost.size
        }
        return 0
    }

    override suspend fun concatenateMyPost(): Int {
        postLocalDataSource.getLastPost()?.let { lastPost ->
            val concatenatePost = getPostStartWith(
                fromUser = prefDataSource.getIdUser(),
                postStartId = lastPost.id,
                size = SIZE_MY_POST_REQUEST
            )
            postLocalDataSource.insertListMyPost(concatenatePost as List<MyPost>)
            return concatenatePost.size
        }
        return 0
    }


    override suspend fun updatePostById(idPost: String) {
        postDataSource.getPost(idPost)?.let {
            postLocalDataSource.updatePost(it)
        }
    }


    override suspend fun updateLikePost(post: SimplePost, isLiked: Boolean) {

        if (!InternetCheck.isNetworkAvailable()) throw Exception(NO_INTERNET_CONNECTION)


        // * toggle like post
        val postFakeUpdate = post.toggleLike()

        // * update fake post
        postLocalDataSource.updatePost(postFakeUpdate)

        // * create notify if is needed
        val currentUser = prefDataSource.getCurrentUser()
        val newNotify = if (post.userPoster?.idUser != prefDataSource.getIdUser() && isLiked)
            post.createLikeNotify(currentUser) else null

        // * update post and send notification
        postDataSource.updateLikes(
            idPost = post.id,
            isLiked = !post.ownerLike,
            notify = newNotify,
            ownerPost = post.userPoster?.idUser!!,
            idUser = currentUser.idUser
        )?.let {
            postLocalDataSource.updatePost(it)
        }

    }

    override suspend fun requestLastPostInitWith(idPost: String) {
        getPostStartWith(
            postStartId = idPost,
            includePost = true,
            size = SIZE_POST_REQUEST
        ).let { listPost ->
            postLocalDataSource.updateAllPost(listPost as List<Post>)
            postLocalDataSource.updateAllMyPost(listPost as List<MyPost>)
        }
    }


    override suspend fun getRealTimePost(idPost: String): Flow<Post?> {
        if (!InternetCheck.isNetworkAvailable()) throw NetworkException()
        return postDataSource.getRealTimePost(idPost)
    }


    override suspend fun addNewPost(post: SimplePost) {
        val idPost = postDataSource.addNewPost(post)
        requestLastPostInitWith(idPost)
    }


    private suspend fun getPostBeforeThat(
        beforePostTimestamp: Date?,
        fromUser: String? = null,
        size: Int
    ): List<SimplePost> {
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
    ): List<SimplePost> {
        if (!InternetCheck.isNetworkAvailable()) throw NetworkException()
        return postDataSource.getLastPost(
            nPosts = size,
            startWithPostId = postStartId,
            fromUserId = fromUser,
            includePost = includePost
        )
    }


    private fun SimplePost.createLikeNotify(myUser: SimpleUser): Notify {
        return Notify(
            userInNotify = myUser,
            idPost = id,
            urlImgPost = urlImage,
            type = TypeNotify.LIKE
        )
    }

    private fun SimplePost.toggleLike(): SimplePost {
        this as Post
        val newCount = if (ownerLike) numberLikes - 1 else numberLikes + 1
        return this.copy(
            numberLikes = newCount,
            ownerLike = !ownerLike
        )
    }

}