package com.nullpointer.blogcompose.models.users

data class InnerUser(
    override var idUser: String = "",
    override var nameUser: String = "",
    override var urlImg: String = "",
) : SimpleUser()