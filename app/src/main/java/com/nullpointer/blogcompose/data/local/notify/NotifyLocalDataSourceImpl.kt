package com.nullpointer.blogcompose.data.local.notify

import com.nullpointer.blogcompose.data.local.cache.NotifyDAO
import com.nullpointer.blogcompose.models.notify.Notify
import kotlinx.coroutines.flow.Flow

class NotifyLocalDataSourceImpl(
    private val notifyDAO: NotifyDAO
) : NotifyLocalDataSource {
    override val listNotify: Flow<List<Notify>> = notifyDAO.getAllNotify()

    override suspend fun insertNotify(notify: Notify) =
        notifyDAO.insertNotify(notify)

    override suspend fun updateNotify(notify: Notify) =
        notifyDAO.updateNotify(notify)

    override suspend fun insertListNotify(listNotify: List<Notify>) =
        notifyDAO.insertListNotify(listNotify)

    override suspend fun updateAllNotify(listNotify: List<Notify>) =
        notifyDAO.updateAllNotify(listNotify)

    override suspend fun deleterAllNotify() =
        notifyDAO.deleterAll()

    override suspend fun getFirstNotify(): Notify? =
        notifyDAO.getFirstNotify()

    override suspend fun getLastNotify(): Notify? =
        notifyDAO.getLastNotify()
}