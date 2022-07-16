package com.nullpointer.blogcompose.domain.comment

import com.nullpointer.blogcompose.models.Comment
import com.nullpointer.blogcompose.models.posts.SimplePost

interface CommentsRepository {
    suspend fun addNewComment(post: SimplePost, comment: String): List<Comment>
    suspend fun getLastComments(idPost: String): List<Comment>
    suspend fun concatenateComments(idPost: String, lastComment: String): List<Comment>
}