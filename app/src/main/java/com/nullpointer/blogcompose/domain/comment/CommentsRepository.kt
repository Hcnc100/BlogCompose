package com.nullpointer.blogcompose.domain.comment

import com.nullpointer.blogcompose.models.Comment
import com.nullpointer.blogcompose.models.posts.Post

interface CommentsRepository {
    suspend fun addNewComment(post: Post, comment: String): List<Comment>
    suspend fun getLastComments(idPost: String): List<Comment>
    suspend fun concatenateComments(idPost: String, lastComment: String): List<Comment>
    suspend fun createComment(comment: String): Comment
}