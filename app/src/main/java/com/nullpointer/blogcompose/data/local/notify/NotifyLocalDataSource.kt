package com.nullpointer.blogcompose.data.local.notify

import com.nullpointer.blogcompose.models.notify.Notify
import kotlinx.coroutines.flow.Flow


interface NotifyLocalDataSource {
    val listNotify: Flow<List<Notify>>

    suspend fun insertNotify(notify: Notify)

    suspend fun updateNotify(notify: Notify)

    suspend fun insertListNotify(listNotify: List<Notify>)

    suspend fun updateAllNotify(listNotify: List<Notify>)

    suspend fun deleterAllNotify()

    suspend fun getFirstNotify(): Notify?

    suspend fun getLastNotify(): Notify?
}