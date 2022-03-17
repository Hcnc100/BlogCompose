package com.nullpointer.blogcompose.models

import java.util.*

interface SimplePost {
    var description: String
    var urlImage: String
    var numberComments: Int
    var numberLikes: Int
    var ownerLike: Boolean
    var timeStamp: Date?
    var id: String
    var poster: Poster?
}