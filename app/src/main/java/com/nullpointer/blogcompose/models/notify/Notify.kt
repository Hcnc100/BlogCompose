package com.nullpointer.blogcompose.models.notify

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp
import com.nullpointer.blogcompose.models.users.InnerUser
import java.util.*

@Entity(tableName = "table_notify")
data class Notify(
    @Embedded
    val userInNotify: InnerUser? = null,
    val idPost: String = "",
    val urlImgPost: String = "",
    @ServerTimestamp
    var timestamp: Date? = null,
    @field:JvmField
    var isOpen: Boolean = false,
    @set:Exclude @get:Exclude
    @PrimaryKey
    var id: String = UUID.randomUUID().toString(),
    var type: TypeNotify = TypeNotify.LIKE,
)