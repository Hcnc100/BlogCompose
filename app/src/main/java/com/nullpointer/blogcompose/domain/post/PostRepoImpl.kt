package com.nullpointer.blogcompose.domain.post

import com.nullpointer.blogcompose.core.utils.InternetCheck
import com.nullpointer.blogcompose.core.utils.NetworkException
import com.nullpointer.blogcompose.data.local.cache.CommentsDAO
import com.nullpointer.blogcompose.data.local.cache.MyPostDAO
import com.nullpointer.blogcompose.data.local.cache.PostDAO
import com.nullpointer.blogcompose.data.remote.post.PostDataSourceImpl
import com.nullpointer.blogcompose.models.Comment
import com.nullpointer.blogcompose.models.posts.MyPost
import com.nullpointer.blogcompose.models.posts.Post
import com.nullpointer.blogcompose.models.posts.SimplePost
import kotlinx.coroutines.flow.Flow
import timber.log.Timber

class PostRepoImpl(
    private val postDataSource: PostDataSourceImpl,
    private val postDAO: PostDAO,
    private val myPostDAO: MyPostDAO,
    private val commentsDAO: CommentsDAO,
) : PostRepository {

    companion object {
        private const val SIZE_POST_REQUEST = 5
        private const val SIZE_COMMENTS = 4
    }

    override val listLastPost: Flow<List<Post>> = postDAO.getAllPost()
    override val listMyLastPost: Flow<List<MyPost>> = myPostDAO.getAllPost()
    override val listComments: Flow<List<Comment>> = commentsDAO.getAllComments()


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

    override suspend fun updateLikePost(post: SimplePost, isLiked: Boolean) {
        val oldPost = postDAO.getPostById(post.id)
        val oldMyPost = myPostDAO.getPostById(post.id)
        try {

            // * update fake post
            if (oldPost != null) postDAO.updatePost(oldPost.copyInnerLike(isLiked))
            if (oldMyPost != null) myPostDAO.updatePost(oldMyPost.copyInnerLike(isLiked))

            // * if has null update post or dont have internet, launch exception
            if (!InternetCheck.isNetworkAvailable()) throw NetworkException()
            val postUpdate = postDataSource.updateLikes(post, isLiked)!!


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

    override suspend fun getLastComments(idPost: String):List<Comment> {
        if (!InternetCheck.isNetworkAvailable()) throw NetworkException()
        return postDataSource.getCommentsForPost(nComments = SIZE_COMMENTS, idPost = idPost)

    }


    override suspend fun addNewComment(post: Post, comment: String) {
        val idComment = postDataSource.addNewComment(post, comment)
        Timber.d("id del commentario $idComment")
        updateAllComments(post.id, idComment)
    }

    private suspend fun updateAllComments(idPost: String, idComment: String) {
        val list = postDataSource.getCommentsForPost(nComments = SIZE_COMMENTS,
            idPost = idPost,
            startWithCommentId = idComment,
            includeComment = true)
        commentsDAO.updateAllComments(list)
    }

    override suspend fun clearComments() {
        commentsDAO.deleterAll()
    }

    suspend fun concatenateComments(idPost: String): List<Comment> {
        if (!InternetCheck.isNetworkAvailable()) throw NetworkException()
        val idComment = commentsDAO.getLastComment()?.id
       return postDataSource.getCommentsForPost(nComments = SIZE_COMMENTS,
            idPost = idPost,
            startWithCommentId = idComment)
    }


    override suspend fun addNewPost(post: Post) {
        val idPost = postDataSource.addNewPost(post)
        requestLastPostInitWith(idPost)
    }

    override suspend fun deleterPost(post: Post) =
        postDataSource.deleterPost(post.id)

    suspend fun deleterInvalidPost(id: String){
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


}