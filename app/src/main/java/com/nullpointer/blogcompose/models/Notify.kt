package com.nullpointer.blogcompose.models

import com.google.firebase.firestore.ServerTimestamp
import java.util.*

data class Notify(
    val idUserLiked: String="",
    val nameUserLiked: String="",
    val imgUserLiked: String="",
    val urlImgPost: String="",
    val timestamp: Date? = null,
    val isOpen: Boolean=false,
) {
    constructor(map: Map<String, String>) : this(
        idUserLiked = map["idUserLiked"]!!,
        nameUserLiked = map["nameUserLiked"]!!,
        imgUserLiked = map["imgUserLiked"]!!,
        urlImgPost = map["urlImgPost"]!!,
    )
}