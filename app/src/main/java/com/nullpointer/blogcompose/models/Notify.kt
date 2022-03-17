package com.nullpointer.blogcompose.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "table_notify")
data class Notify(
    val idUserLiked: String = "",
    val nameUserLiked: String = "",
    val imgUserLiked: String = "",
    val urlImgPost: String = "",
    var timestamp: Date? = null,
    val isOpen: Boolean = false,
    @PrimaryKey
    var id: String = "",
) {
    constructor(map: Map<String, String>) : this(
        idUserLiked = map["idUserLiked"]!!,
        nameUserLiked = map["nameUserLiked"]!!,
        imgUserLiked = map["imgUserLiked"]!!,
        urlImgPost = map["urlImgPost"]!!,
    )
}