package com.nullpointer.blogcompose.models.posts

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp
import com.nullpointer.blogcompose.models.users.SimpleUser
import kotlinx.parcelize.Parcelize
import java.util.*


@Entity(tableName = "table_post")
@Parcelize
data class Post(
    override var description: String = "",
    override var urlImage: String = "",
    override var numberComments: Int = 0,
    override var numberLikes: Int = 0,
    @Embedded
    override var userPoster: SimpleUser? = null,
    @set:Exclude @get:Exclude
    override var ownerLike: Boolean = false,
    @ServerTimestamp
    override var timestamp: Date? = null,
    @set:Exclude @get:Exclude
    @PrimaryKey
    override var id: String = UUID.randomUUID().toString(),
) : SimplePost, Parcelable {
}

