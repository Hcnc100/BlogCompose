package com.nullpointer.blogcompose.domain.post

import com.nullpointer.blogcompose.core.utils.InternetCheck
import com.nullpointer.blogcompose.core.utils.NetworkException
import com.nullpointer.blogcompose.data.local.cache.MyPostDAO
import com.nullpointer.blogcompose.data.local.cache.PostDAO
import com.nullpointer.blogcompose.data.remote.post.PostDataSourceImpl
import com.nullpointer.blogcompose.domain.auth.AuthRepoImpl
import com.nullpointer.blogcompose.models.Comment
import com.nullpointer.blogcompose.models.notify.Notify
import com.nullpointer.blogcompose.models.notify.TypeNotify
import com.nullpointer.blogcompose.models.posts.MyPost
import com.nullpointer.blogcompose.models.posts.Post
import com.nullpointer.blogcompose.models.posts.SimplePost
import com.nullpointer.blogcompose.models.users.InnerUser
import com.nullpointer.blogcompose.models.users.MyUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import timber.log.Timber

class PostRepoImpl(
    private val postDataSource: PostDataSourceImpl,
    private val postDAO: PostDAO,
    private val myPostDAO: MyPostDAO,
    private val authRepoImpl: AuthRepoImpl
) : PostRepository {

    companion object {
        private const val SIZE_POST_REQUEST = 7
        private const val SIZE_COMMENTS = 7
    }

    override val listLastPost: Flow<List<Post>> = postDAO.getAllPost()
    override val listMyLastPost: Flow<List<MyPost>> = myPostDAO.getAllPost()


    override suspend fun requestLastPost(forceRefresh: Boolean): Int {
        if (!InternetCheck.isNetworkAvailable()) throw NetworkException()
        val firstPost = if (forceRefresh) null else postDAO.getFirstPost()
        val listLastPost = postDataSource.getLastPostDate(
            date = firstPost?.timestamp,
            nPosts = SIZE_POST_REQUEST
        )
        if (listLastPost.isNotEmpty()) postDAO.updateAllPost(listLastPost)
        return listLastPost.size
    }


    override suspend fun requestMyLastPost(forceRefresh: Boolean): Int {
        if (!InternetCheck.isNetworkAvailable()) throw NetworkException()
        // * get last post consideration "my" first post saved in database
        // ? and remove repeater info because owner is me
        val firstPost = if (forceRefresh) null else myPostDAO.getFirstPost()
        val listMyLastPost = postDataSource.getMyLastPostDate(
            date = firstPost?.timestamp,
            nPosts = SIZE_POST_REQUEST
        ).map { MyPost.fromPost(it) }
        if (listMyLastPost.isNotEmpty()) myPostDAO.updateAllPost(listMyLastPost)
        return listMyLastPost.size
    }

    override suspend fun concatenatePost(): Int {
        // * get post and concatenate to all post
        // * this nos remove old post
        // * consideration first post saved
        if (!InternetCheck.isNetworkAvailable()) throw NetworkException()
        val listNewPost = postDataSource.getLastPost(
            nPosts = SIZE_POST_REQUEST,
            startWithPostId = postDAO.getLastPost()?.id
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
            startWithPostId = myPostDAO.getLastPost()?.id
        )
        val listSimplePost = listMyNewPost.map { MyPost.fromPost(it) }
        myPostDAO.insertListPost(listSimplePost)
        return listSimplePost.size
    }

    override suspend fun getLastPostByUser(idUser: String, inCaching: Boolean): List<Post> {
        TODO("Not yet implemented")
    }


    override suspend fun updatePost(idPost: String) {
        val oldPost = postDAO.getPostById(idPost)
        val oldMyPost = myPostDAO.getPostById(idPost)
        try {
            val postUpdate = postDataSource.getPost(idPost)!!

            if (oldPost != null) postDAO.updatePost(postUpdate)
            if (oldMyPost != null) myPostDAO.updatePost(MyPost.fromPost(postUpdate))
        } catch (e: Exception) {
            // ? if has problem restore data post
            oldPost?.let { postDAO.updatePost(oldPost) }
            oldMyPost?.let { myPostDAO.updatePost(it) }

            throw e
        }
    }

    override suspend fun updateLikePost(post: SimplePost) {
        val oldPost = postDAO.getPostById(post.id)
        val oldMyPost = myPostDAO.getPostById(post.id)
        val currentUser = authRepoImpl.myUser.first()
        val newNotify = post.createLikeNotify(currentUser)

        try {

            // * update fake post
            if (oldPost != null) postDAO.updatePost(oldPost.toggleLike())
            if (oldMyPost != null) myPostDAO.updatePost(oldMyPost.toggleLike())

            // * if has null update post or dont have internet, launch exception
            if (!InternetCheck.isNetworkAvailable()) throw NetworkException()
            val postUpdate = postDataSource.updateLikes(
                idPost = post.id,
                isLiked = !post.ownerLike,
                notify = newNotify,
                ownerPost = post.userPoster?.idUser!!,
                idUser = currentUser.idUser
            )!!


            // * update the info from post
            if (oldPost != null) postDAO.updatePost(postUpdate)
            if (oldMyPost != null) myPostDAO.updatePost(MyPost.fromPost(postUpdate))
        } catch (e: Exception) {
            // ? if has problem restore data post
            oldPost?.let { postDAO.updatePost(it) }
            oldMyPost?.let { myPostDAO.updatePost(it) }

            throw e
        }
    }

    override suspend fun requestLastPostInitWith(idPost: String) {
        if (!InternetCheck.isNetworkAvailable()) throw NetworkException()
        val listLastPost = postDataSource.getLastPost(
            nPosts = SIZE_POST_REQUEST,
            startWithPostId = idPost,
            includePost = true
        )
        if (listLastPost.isNotEmpty()) postDAO.updateAllPost(listLastPost)

        val listMyPost = postDataSource.getMyLastPost(
            nPosts = SIZE_POST_REQUEST,
            startWithPostId = idPost,
            includePost = true
        )
        if (listLastPost.isNotEmpty()) myPostDAO.updateAllPost(listMyPost.map { MyPost.fromPost(it) })

    }

    override suspend fun deleterAllPost() {
        postDAO.deleterAll()
        myPostDAO.deleterAll()
    }

    override suspend fun getRealTimePost(idPost: String): Flow<Post?> {
        if (!InternetCheck.isNetworkAvailable()) throw NetworkException()
        return postDataSource.getRealTimePost(idPost)
    }

    override suspend fun getLastComments(idPost: String): List<Comment> {
        if (!InternetCheck.isNetworkAvailable()) throw NetworkException()
        return postDataSource.getCommentsForPost(nComments = SIZE_COMMENTS, idPost = idPost)

    }


    override suspend fun addNewComment(post: SimplePost, comment: String): List<Comment> {
        val currentUser = authRepoImpl.myUser.first()
        val newComment = Comment(
            userComment = currentUser.toInnerUser(),
            comment = comment
        )
        val notify = post.createCommentNotify(currentUser)
        val idComment =
            postDataSource.addNewComment(post.id, post.userPoster?.idUser!!, newComment, notify)
        Timber.d("id del commentario $idComment")
        return updateAllComments(post.id, idComment)
    }

    private suspend fun updateAllComments(idPost: String, idComment: String): List<Comment> {
        return postDataSource.getCommentsForPost(
            nComments = SIZE_COMMENTS,
            idPost = idPost,
            startWithCommentId = idComment,
            includeComment = true
        )
    }



    suspend fun concatenateComments(idPost: String, lastComment: String): List<Comment> {
        if (!InternetCheck.isNetworkAvailable()) throw NetworkException()
        return postDataSource.getCommentsForPost(
            nComments = SIZE_COMMENTS,
            idPost = idPost,
            startWithCommentId = lastComment
        )
    }


    override suspend fun addNewPost(post: Post) {
        val idPost = postDataSource.addNewPost(post)
        requestLastPostInitWith(idPost)
    }

    override suspend fun deleterPost(post: Post) =
        postDataSource.deleterPost(post.id)

    suspend fun deleterInvalidPost(id: String) {
        postDAO.deleterPost(id)
        myPostDAO.deleterPost(id)
    }

    override suspend fun updateInnerPost(post: Post) {
        val oldPost = postDAO.getPostById(post.id)
        val oldMyPost = myPostDAO.getPostById(post.id)
        if (oldPost != null) postDAO.updatePost(post)
        if (oldMyPost != null) myPostDAO.updatePost(MyPost.fromPost(post))
    }

    override suspend fun getPost(idPost: String) =
        postDataSource.getPost(idPost)


    private fun MyUser.toInnerUser(): InnerUser {
        return InnerUser(
            idUser = idUser,
            name = name,
            urlImg = urlImg
        )
    }

    private fun SimplePost.createLikeNotify(myUser: MyUser): Notify {
        return Notify(
            userInNotify = myUser.toInnerUser(),
            idPost = id,
            urlImgPost = urlImage,
            type = TypeNotify.LIKE
        )
    }

    private fun SimplePost.createCommentNotify(myUser: MyUser): Notify {
        return Notify(
            userInNotify = myUser.toInnerUser(),
            idPost = id,
            urlImgPost = urlImage,
            type = TypeNotify.COMMENT
        )
    }
}