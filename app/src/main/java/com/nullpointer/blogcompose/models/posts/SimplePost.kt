package com.nullpointer.blogcompose.models.posts

import com.nullpointer.blogcompose.models.users.SimpleUser
import java.util.*

interface SimplePost {
    var description: String
    var urlImage: String
    var numberComments: Int
    var numberLikes: Int
    var ownerLike: Boolean
    var timestamp: Date?
    var id: String
    var userPoster: SimpleUser?
}