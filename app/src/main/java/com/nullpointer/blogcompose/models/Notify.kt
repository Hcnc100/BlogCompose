package com.nullpointer.blogcompose.models

data class Notify(
    val idUserLiked: String,
    val nameUserLiked: String,
    val imgUserLiked: String,
    val urlImgPost: String,
) {
    constructor(map: Map<String, String>) : this(
        idUserLiked = map["idUserLiked"]!!,
        nameUserLiked = map["nameUserLiked"]!!,
        imgUserLiked = map["imgUserLiked"]!!,
        urlImgPost = map["urlImgPost"]!!
    )
}