package com.nullpointer.blogcompose.models

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
@Entity(tableName = "table_post")
data class Post(
    val description: String = "",
    val urlImage: String = "",
    val numberComments: Int = 0,
    var numberLikes: Int = 0,
    @Embedded
    val poster: Poster? = null,
    @set:Exclude @get:Exclude
    var ownerLike: Boolean = false,
    @ServerTimestamp
    var timeStamp: Date? = null,
    @set:Exclude @get:Exclude
    @PrimaryKey
    var id: String = UUID.randomUUID().toString(),
) : Parcelable

