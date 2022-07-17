package com.nullpointer.blogcompose.data.remote.notify

import com.nullpointer.blogcompose.models.notify.Notify
import java.util.*

interface NotifyDataSource {

    suspend fun getLastNotifications(
        startWith: String? = null,
        endWith: String? = null,
        numberRequest: Int = Integer.MAX_VALUE,
        includeNotify: Boolean = false
    ): List<Notify>

    suspend fun getLastNotifyBeforeThat(
        numberRequest: Int,
        date: Date?
    ): List<Notify>

    fun updateOpenNotify(idNotify: String)
}