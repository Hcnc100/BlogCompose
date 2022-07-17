package com.nullpointer.blogcompose.domain.comment

import com.nullpointer.blogcompose.core.utils.InternetCheck
import com.nullpointer.blogcompose.core.utils.NetworkException
import com.nullpointer.blogcompose.data.local.preferences.PreferencesDataSource
import com.nullpointer.blogcompose.data.remote.comment.CommentsDataSource
import com.nullpointer.blogcompose.models.Comment
import com.nullpointer.blogcompose.models.notify.Notify
import com.nullpointer.blogcompose.models.notify.TypeNotify
import com.nullpointer.blogcompose.models.posts.SimplePost
import com.nullpointer.blogcompose.models.users.MyUser

class CommentsRepoImpl(
    private val prefDataSource: PreferencesDataSource,
    private val commentDataSource: CommentsDataSource
):CommentsRepository {

    companion object {
        private const val SIZE_COMMENTS = 7
    }
    override suspend fun addNewComment(post: SimplePost, comment: String): List<Comment> {
        val currentUser = prefDataSource.getCurrentUser()
        val notify = if (post.userPoster?.idUser == prefDataSource.getIdUser())
            null else post.createCommentNotify(currentUser)
        val newComment = currentUser.createNewComment(comment)
        val idComment = commentDataSource.addNewComment(
            idPost = post.id,
            ownerPost = post.userPoster?.idUser!!,
            comment = newComment,
            notify = notify
        )
        return getLastCommentsInitWith(post.id, idComment)
    }

    private suspend fun getLastCommentsInitWith(idPost: String, idComment: String): List<Comment> {
        return commentDataSource.getCommentsForPost(
            nComments = SIZE_COMMENTS,
            idPost = idPost,
            startWithCommentId = idComment,
            includeComment = true
        )
    }

    override suspend fun getLastComments(idPost: String): List<Comment> {
        if (!InternetCheck.isNetworkAvailable()) throw NetworkException()
        return commentDataSource.getCommentsForPost(nComments = SIZE_COMMENTS, idPost = idPost)
    }

    override suspend fun concatenateComments(idPost: String, lastComment: String): List<Comment> {
        if (!InternetCheck.isNetworkAvailable()) throw NetworkException()
        return commentDataSource.getCommentsForPost(
            nComments = SIZE_COMMENTS,
            idPost = idPost,
            startWithCommentId = lastComment
        )
    }

    private fun MyUser.createNewComment(newComment: String): Comment {
        return Comment(
            userComment = this.toInnerUser(),
            comment = newComment
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
