package com.nullpointer.blogcompose.models

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp
import java.util.*

data class Post(
    @set:Exclude @get:Exclude
    var id:String="",
    val profilePicture:String="",
    val description:String="",
    val postOwnerName:String="",
    val postOwnerId:String="",
    @ServerTimestamp
    var timeStamp: Date?=null,
    val urlImage:String=""
)