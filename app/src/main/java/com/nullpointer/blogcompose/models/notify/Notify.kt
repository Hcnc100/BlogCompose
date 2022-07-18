package com.nullpointer.blogcompose.models.notify

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp
import com.nullpointer.blogcompose.models.users.SimpleUser
import java.util.*

@Entity(tableName = "table_notify")
data class Notify(
    @Embedded
    val userInNotify: SimpleUser? = null,
    val idPost: String = "",
    val urlImgPost: String = "",
    @ServerTimestamp
    val timestamp: Date? = null,
    @field:JvmField
    var isOpen: Boolean = false,
    @get:Exclude
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val type: TypeNotify = TypeNotify.LIKE,
)