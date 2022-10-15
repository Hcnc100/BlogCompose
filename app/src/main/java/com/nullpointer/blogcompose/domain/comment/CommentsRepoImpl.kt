package com.nullpointer.blogcompose.domain.comment

import com.nullpointer.blogcompose.core.utils.callApiTimeOut
import com.nullpointer.blogcompose.data.local.preferences.PreferencesDataSource
import com.nullpointer.blogcompose.data.remote.comment.CommentsDataSource
import com.nullpointer.blogcompose.models.Comment
import com.nullpointer.blogcompose.models.notify.Notify
import com.nullpointer.blogcompose.models.notify.TypeNotify
import com.nullpointer.blogcompose.models.posts.SimplePost
import com.nullpointer.blogcompose.models.users.AuthUser
import com.nullpointer.blogcompose.models.users.SimpleUser

class CommentsRepoImpl(
    private val prefDataSource: PreferencesDataSource,
    private val commentDataSource: CommentsDataSource
):CommentsRepository {

    companion object {
        private const val SIZE_COMMENTS = 7L
    }

    override suspend fun createComment(comment: String): Comment {
        val currentUser = prefDataSource.getCurrentUser()
        return currentUser.createNewComment(comment)
    }

    override suspend fun addNewComment(post: SimplePost, comment: String): List<Comment> {
        return callApiTimeOut {
            val currentUser = prefDataSource.getCurrentUser()
            val notify = createNewNotify(currentUser, post)
            val newComment = currentUser.createNewComment(comment)
            val idComment = commentDataSource.addNewComment(
                idPost = post.id,
                ownerPost = post.userPoster?.idUser!!,
                comment = newComment,
                notify = notify
            )
            getLastCommentsInitWith(post.id, idComment)
        }
    }

    private suspend fun getLastCommentsInitWith(idPost: String, idComment: String): List<Comment> {
        return callApiTimeOut {
            commentDataSource.getLastCommentFromPost(
                idPost = idPost,
                numberComments = SIZE_COMMENTS,
                includeComment = true,
                idComment = idComment
            ).reversed()
        }
    }

    override suspend fun getLastComments(idPost: String): List<Comment> {
        return callApiTimeOut {
            commentDataSource.getLastCommentFromPost(
                idPost = idPost,
                numberComments = SIZE_COMMENTS
            ).reversed()
        }
    }

    override suspend fun concatenateComments(idPost: String, lastComment: String): List<Comment> {
        return callApiTimeOut {
            commentDataSource.getListConcatenateComments(
                numberComments = SIZE_COMMENTS,
                idPost = idPost,
                idComment = lastComment
            ).reversed()
        }
    }

    private suspend fun createNewNotify(
        currentUser: AuthUser,
        post: SimplePost
    ): Notify? {
        return if (post.userPoster?.idUser == prefDataSource.getIdUser())
            null
        else
            post.createCommentNotify(currentUser)
    }

    private fun AuthUser.createNewComment(newComment: String): Comment {
        return Comment(
            userComment = SimpleUser(
                idUser = id,
                name = name,
                urlImg = urlImg
            ),
            comment = newComment
        )
    }


    private fun SimplePost.createCommentNotify(myUser: AuthUser): Notify {
        return Notify(
            userInNotify = SimpleUser(
                idUser = myUser.id,
                name = myUser.name,
                urlImg = myUser.urlImg
            ),
            idPost = id,
            urlImgPost = urlImage,
            type = TypeNotify.COMMENT
        )
    }
}
