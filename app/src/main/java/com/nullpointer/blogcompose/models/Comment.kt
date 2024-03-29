package com.nullpointer.blogcompose.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp
import com.nullpointer.blogcompose.models.users.SimpleUser
import java.util.*

@Entity(tableName = "table_comments")
data class Comment(
    @Embedded
    val userComment: SimpleUser? = null,
    val comment: String = "",
    @ServerTimestamp
    val timestamp: Date? = null,
    @PrimaryKey
    @get:Exclude
    val id: String = UUID.randomUUID().toString(),
)