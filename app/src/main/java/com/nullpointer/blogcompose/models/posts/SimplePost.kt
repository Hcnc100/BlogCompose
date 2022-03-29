package com.nullpointer.blogcompose.models.posts

import com.nullpointer.blogcompose.models.users.InnerUser
import java.util.*

abstract class SimplePost {
    open var description: String=""
    open var urlImage: String=""
    open var numberComments: Int=0
    open var numberLikes: Int=0
    open var ownerLike: Boolean=false
    open var timestamp: Date?=null
    open var id: String=""
    open var userPoster: InnerUser?=null
}