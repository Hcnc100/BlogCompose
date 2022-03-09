package com.nullpointer.blogcompose.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp
import java.util.*

@Entity(tableName = "myPosts")
data class MyPost(
    val description: String = "",
    val urlImage: String = "",
    val numberComments: Int = 0,
    var numberLikes: Int = 0,
    @set:Exclude @get:Exclude
    var ownerLike: Boolean = false,
    @ServerTimestamp
    var timeStamp: Date? = null,
    @set:Exclude @get:Exclude
    @PrimaryKey
    var id: String = UUID.randomUUID().toString(),
)