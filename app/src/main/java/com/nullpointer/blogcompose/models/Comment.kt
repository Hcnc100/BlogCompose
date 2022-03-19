package com.nullpointer.blogcompose.models

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp
import java.util.*

data class Comment(
    val urlImg: String = "",
    val nameUser: String = "",
    val comment: String = "",
    @ServerTimestamp
    var timestamp: Date? = null,
    @set:Exclude @get:Exclude
    var id: String=UUID.randomUUID().toString()
)