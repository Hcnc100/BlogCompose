package com.nullpointer.blogcompose.models.users

abstract class SimpleUser(
    @Transient
    open var idUser: String = "",
    @Transient
    open var name: String = "",
    @Transient
    open var urlImg: String = "",
)