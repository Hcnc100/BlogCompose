package com.nullpointer.blogcompose.domain.notify

import com.nullpointer.blogcompose.models.notify.Notify
import kotlinx.coroutines.flow.Flow

interface NotifyRepository {
    val listNotify: Flow<List<Notify>>
    suspend fun deleterAllNotify()
    suspend fun concatenateNotify(): Int
    suspend fun openNotify(notify: Notify)
    suspend fun requestLastNotifyStartWith(idNotify: String)
    suspend fun requestLastNotify(forceRefresh: Boolean = false): Int

}