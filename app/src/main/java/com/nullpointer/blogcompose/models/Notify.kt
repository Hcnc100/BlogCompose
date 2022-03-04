package com.nullpointer.blogcompose.models

class NotifyBasic(map: Map<String, String>) {
    val idUserLiked by map
    val nameUserLiked by map
    val imgUserLiked by map
    val urlImgPost by map
    override fun toString(): String {
        return "NotifyBasic(idUserLiked='$idUserLiked', nameUserLiked='$nameUserLiked', imgUserLiked='$imgUserLiked', urlImgPost='$urlImgPost')"
    }


}