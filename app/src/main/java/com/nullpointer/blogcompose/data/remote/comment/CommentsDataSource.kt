package com.nullpointer.blogcompose.data.remote.comment

import com.nullpointer.blogcompose.models.Comment
import com.nullpointer.blogcompose.models.notify.Notify

interface CommentsDataSource {

    suspend fun getCommentsForPost(
        nComments: Int = Integer.MAX_VALUE,
        startWithCommentId: String? = null,
        endWithCommentId: String? = null,
        includeComment: Boolean = false,
        idPost: String,
    ): List<Comment>

    suspend fun addNewComment(
        idPost: String,
        ownerPost: String,
        comment: Comment,
        notify: Notify?
    ): String
}