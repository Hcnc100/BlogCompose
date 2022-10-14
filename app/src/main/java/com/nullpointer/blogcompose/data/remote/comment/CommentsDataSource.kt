package com.nullpointer.blogcompose.data.remote.comment

import com.nullpointer.blogcompose.models.Comment
import com.nullpointer.blogcompose.models.notify.Notify

interface CommentsDataSource {

    suspend fun getLastCommentFromPost(
        idPost: String,
        numberComments: Long = Long.MAX_VALUE,
        includeComment: Boolean = false,
        idComment: String? = null
    ): List<Comment>

    suspend fun getListConcatenateComments(
        idPost: String,
        numberComments: Long,
        idComment: String
    ): List<Comment>

    suspend fun addNewComment(
        idPost: String,
        ownerPost: String,
        comment: Comment,
        notify: Notify?
    ): String
}