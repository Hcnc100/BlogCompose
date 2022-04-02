package com.nullpointer.blogcompose.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp
import com.nullpointer.blogcompose.models.posts.StatePost
import com.nullpointer.blogcompose.models.users.InnerUser
import java.util.*

@Entity(tableName = "table_comments")
data class Comment(
    @Embedded
    var userComment:InnerUser?=null,
    var comment: String = "",
    @ServerTimestamp
    var timestamp: Date? = null,
    @PrimaryKey
    @set:Exclude @get:Exclude
    var id: String=UUID.randomUUID().toString(),
    @Ignore
    var stateValidate: String= StatePost.VALIDATING
)