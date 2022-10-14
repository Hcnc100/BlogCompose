package com.nullpointer.blogcompose.data.remote.notify

import com.nullpointer.blogcompose.models.notify.Notify

interface NotifyRemoteDataSource {

    suspend fun getLastNotifications(
        idNotify: String? = null,
        numberRequest: Long = Long.MAX_VALUE,
        includeNotify: Boolean = false
    ): List<Notify>

    suspend fun getConcatenateNotify(
        numberRequest: Long = Long.MAX_VALUE,
        idNotify: String
    ): List<Notify>

    fun updateOpenNotify(idNotify: String)
}