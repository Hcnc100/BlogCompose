package com.nullpointer.blogcompose.models.users

import android.os.Parcelable
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.parcelize.Parcelize
import java.io.Serializable
import java.util.*


@Parcelize
data class AuthUser(
    @get:Exclude
    val id: String = "",
    val name: String = "",
    val urlImg: String = "",
    @get:Exclude
    val token: String = "",
    @ServerTimestamp
    val timeCreate: Date? = null,
    @ServerTimestamp
    val timeUpdate: Date? = null
) : Parcelable, Serializable