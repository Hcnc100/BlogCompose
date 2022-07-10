package com.nullpointer.blogcompose.models.users

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class InnerUser(
    override var idUser: String = "",
    override var name: String = "",
    override var urlImg: String = "",
) : SimpleUser(), Parcelable