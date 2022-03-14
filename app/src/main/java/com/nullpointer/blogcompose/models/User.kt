package com.nullpointer.blogcompose.models

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp
import java.util.*

data class User(
    @set:Exclude @get:Exclude
    var uuid: String = "",
    val token: String = "",
    val nameUser: String = "",
    val urlImg: String = "",
    @ServerTimestamp
    val timeCreate: Date? = null,
)