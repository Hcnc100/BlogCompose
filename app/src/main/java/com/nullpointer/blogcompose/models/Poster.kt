package com.nullpointer.blogcompose.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Poster(
    val uuid: String = "",
    val name: String = "",
    val urlImg: String = "",
) : Parcelable
