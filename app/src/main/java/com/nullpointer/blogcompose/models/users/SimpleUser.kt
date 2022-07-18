package com.nullpointer.blogcompose.models.users

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.Serializable


@Parcelize
data class SimpleUser(
    val idUser: String = "",
    val name: String = "",
    val urlImg: String = "",
) : Parcelable, Serializable