package com.nullpointer.blogcompose.models.posts

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp
import com.nullpointer.blogcompose.models.users.InnerUser
import java.util.*

@Entity(tableName = "table_my_post")
data class MyPost(
    override var description: String = "",
    override var urlImage: String = "",
    override var numberComments: Int = 0,
    override var numberLikes: Int = 0,
    @Ignore
    override var userPoster: InnerUser? = null,
    @set:Exclude @get:Exclude
    override var ownerLike: Boolean = false,
    @ServerTimestamp
    override var timestamp: Date? = null,
    @set:Exclude @get:Exclude
    @PrimaryKey
    override var id: String = UUID.randomUUID().toString(),
) : SimplePost() {
        companion object {
            fun fromPost(post: Post) = MyPost(
                description = post.description,
                urlImage = post.urlImage,
                numberComments = post.numberComments,
                numberLikes = post.numberLikes,
                ownerLike = post.ownerLike,
                timestamp  = post.timestamp,
                id = post.id
            )
        }


        fun copyInnerLike(isLiked: Boolean): MyPost {
        val newCount = if (isLiked) numberLikes + 1 else numberLikes - 1
        return this.copy(
            numberLikes = newCount,
            ownerLike = isLiked
        )
    }
}