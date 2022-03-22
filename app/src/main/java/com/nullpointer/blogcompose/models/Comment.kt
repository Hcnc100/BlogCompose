package com.nullpointer.blogcompose.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp
import java.util.*

@Entity(tableName = "table_comments")
data class Comment(
    val urlImg: String = "",
    val nameUser: String = "",
    val comment: String = "",
    @ServerTimestamp
    var timestamp: Date? = null,
    @PrimaryKey
    @set:Exclude @get:Exclude
    var id: String=UUID.randomUUID().toString()
)