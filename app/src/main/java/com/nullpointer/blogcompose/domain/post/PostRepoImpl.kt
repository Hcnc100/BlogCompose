package com.nullpointer.blogcompose.domain.post

import android.content.Context
import android.widget.Toast
import com.nullpointer.blogcompose.core.utils.InternetCheck
import com.nullpointer.blogcompose.core.utils.NetworkException
import com.nullpointer.blogcompose.data.local.cache.CommentsDAO
import com.nullpointer.blogcompose.data.local.cache.MyPostDAO
import com.nullpointer.blogcompose.data.local.cache.PostDAO
import com.nullpointer.blogcompose.data.remote.PostDataSource
import com.nullpointer.blogcompose.models.Comment
import com.nullpointer.blogcompose.models.MyPost
import com.nullpointer.blogcompose.models.Post
import kotlinx.coroutines.flow.Flow

class PostRepoImpl(
    private val postDataSource: PostDataSource,
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
            date = firstPost?.timeStamp,
            nPosts = SIZE_POST_REQUEST
        )
        if (listLastPost.isNotEmpty()) {
            if (listLastPost.size == SIZE_POST_REQUEST) {
                postDAO.updateAllPost(listLastPost)
            } else {
                postDAO.updateListPost(SIZE_POST_REQUEST, listLastPost)
            }

        }
        return listLastPost.size
    }


    override suspend fun requestMyLastPost(forceRefresh: Boolean): Int {
        if (!InternetCheck.isNetworkAvailable()) throw NetworkException()
        // * get last post consideration "my" first post saved in database
        // ? and remove repeater info because owner is me
        val firstPost = if (forceRefresh) null else myPostDAO.getFirstPost()
        val listMyLastPost = postDataSource
            .getMyLastPostDate(
                date = firstPost?.timeStamp,
                nPosts = SIZE_POST_REQUEST
            )
            .map { MyPost.fromPost(it) }
        if (listMyLastPost.size == SIZE_POST_REQUEST) {
            myPostDAO.updateAllPost(listMyLastPost)
        } else {
            myPostDAO.updateListPost(SIZE_POST_REQUEST, listMyLastPost)
        }

        return listMyLastPost.size
    }

    override suspend fun concatenatePost(): Int {
        // * get post and concatenate to all post
        // * this nos remove old post
        // * consideration first post saved
        if (!InternetCheck.isNetworkAvailable()) throw NetworkException()
        val listNewPost = postDataSource.getLatestPost(
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


    override suspend fun updateLikePost(idPost: String, isLiked: Boolean?) {
        val oldPost = postDAO.getPostById(idPost)
        val oldMyPost = myPostDAO.getPostById(idPost)
        try {
            val postUpdate = if (isLiked != null) {
                // * update fake post
                if (oldPost != null) postDAO.updatePost(oldPost.copyInnerLike(isLiked))
                if (oldMyPost != null) myPostDAO.updatePost(oldMyPost.copyInnerLike(isLiked))

                // * if has null update post or dont have internet, launch exception
                if (!InternetCheck.isNetworkAvailable()) throw NetworkException()
                postDataSource.updateLikes(idPost, isLiked)!!
            } else {
                postDataSource.getPost(idPost)!!
            }

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

    private suspend fun requestLastPostInitWith(idPost: String) {
        if (!InternetCheck.isNetworkAvailable()) throw NetworkException()
        val listLastPost = postDataSource.getLatestPost(
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

    override suspend fun getRealTimePost(idPost: String): Flow<Post?> =
        postDataSource.getRealTimePost(idPost)

    override suspend fun getLastComments(idPost: String) {

        val listComments =
            postDataSource.getCommentsForPost(nComments = SIZE_COMMENTS, idPost = idPost)
        commentsDAO.updateAllComments(listComments)
    }


    override suspend fun addNewComment(idPost: String, comment: String) {
        val idComment = postDataSource.addNewComment(idPost, comment)
        val list = postDataSource.getCommentsForPost(nComments = SIZE_COMMENTS,
            idPost = idPost,
            startWithCommentId = idComment,
            includePost = true)
        commentsDAO.updateAllComments(list)
    }

    override suspend fun clearComments() {
        commentsDAO.deleterAll()
    }

    suspend fun concatenateComments(idPost: String) {
        if (!InternetCheck.isNetworkAvailable()) throw NetworkException()
        val idComment = commentsDAO.getLastComment()?.id
        val list = postDataSource.getCommentsForPost(nComments = SIZE_COMMENTS,
            idPost = idPost,
            startWithCommentId = idComment)
        commentsDAO.insertListComments(list)
    }


    override suspend fun addNewPost(post: Post, context: Context) {
        postDataSource.addNewPost(post)
        Toast.makeText(context, "Post subido con exito", Toast.LENGTH_SHORT).show()
        requestLastPostInitWith(post.id)
    }

    override suspend fun deleterPost(post: Post) =
        postDataSource.deleterPost(post)

    override suspend fun updateInnerPost(post: Post) {
        val oldPost = postDAO.getPostById(post.id)
        val oldMyPost = myPostDAO.getPostById(post.id)
        if (oldPost != null) postDAO.updatePost(post)
        if (oldMyPost != null) myPostDAO.updatePost(MyPost.fromPost(post))
    }

    override suspend fun getPost(idPost: String) =
        postDataSource.getPost(idPost)


}