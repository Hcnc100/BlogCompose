package com.nullpointer.blogcompose.models.notify

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.nullpointer.blogcompose.models.users.InnerUser
import java.util.*

@Entity(tableName = "table_notify")
data class Notify(
    @Embedded
    val userInNotify: InnerUser? = null,
    val idPost: String = "",
    val urlImgPost: String = "",
    var timestamp: Date? = null,
    var isOpen: Boolean = false,
    @PrimaryKey
    var id: String = "",
    var typeNotify: TypeNotify = TypeNotify.LIKE,
)