package com.nullpointer.blogcompose.models

import java.util.*

data class CurrentUser(
    val uuid: String,
    val nameUser: String?,
    val urlImg:String?
)