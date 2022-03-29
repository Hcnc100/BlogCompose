package com.nullpointer.blogcompose.models.users

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp
import java.util.*

data class MyUser(
    @set:Exclude @get:Exclude
    override var idUser: String = "",
    override var nameUser: String = "",
    override var urlImg: String = "",
    var token: String = "",
    @ServerTimestamp
    var timeCreate: Date? = null,
    @ServerTimestamp
    var timeUpdate: Date? = null,
) : SimpleUser()