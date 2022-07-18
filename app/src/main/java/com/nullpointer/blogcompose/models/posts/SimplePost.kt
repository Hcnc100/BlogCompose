package com.nullpointer.blogcompose.models.posts

import com.nullpointer.blogcompose.models.users.SimpleUser
import java.util.*

abstract class SimplePost {
    abstract var description: String
    abstract var urlImage: String
    abstract var numberComments: Int
    abstract var numberLikes: Int
    abstract var ownerLike: Boolean
    abstract var timestamp: Date?
    abstract var id: String
    abstract var userPoster: SimpleUser?
}