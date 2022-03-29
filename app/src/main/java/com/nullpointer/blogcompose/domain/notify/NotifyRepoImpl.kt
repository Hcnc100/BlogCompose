package com.nullpointer.blogcompose.domain.notify

import com.nullpointer.blogcompose.core.utils.InternetCheck
import com.nullpointer.blogcompose.core.utils.NetworkException
import com.nullpointer.blogcompose.data.local.cache.NotifyDAO
import com.nullpointer.blogcompose.data.remote.NotifyDataSource
import com.nullpointer.blogcompose.models.notify.Notify
import kotlinx.coroutines.flow.Flow

class NotifyRepoImpl(
    private val notifyDataSource: NotifyDataSource,
    private val notifyDAO: NotifyDAO,
) : NotifyRepository {

    companion object {
        // * size of request notify data source
        private const val SIZE_NOTIFY_REQUEST = 10
    }

    // * return flow to notification in database, is for get modifications in realtime
    override val listNotify: Flow<List<Notify>> = notifyDAO.getAllNotify()

    override suspend fun requestLastNotify(forceRefresh: Boolean): Int {
        if (!InternetCheck.isNetworkAvailable()) throw NetworkException()
        // * get the first id from notify order for date
        // ? if the database is empty o the parameter "force refresh" is true
        // ? request new data and replace data in database
        val firstNotify = if (forceRefresh) null else notifyDAO.getFirstNotify()
        val listNewNotify = notifyDataSource.getLastNotifyDate(
            numberRequest = SIZE_NOTIFY_REQUEST,
            date = firstNotify?.timestamp)
        if (listNewNotify.isNotEmpty()) notifyDAO.updateAllNotify(listNewNotify)
        return listNewNotify.size
    }

    override suspend fun concatenateNotify(): Int {
        if (!InternetCheck.isNetworkAvailable()) throw NetworkException()
        // * get last notify consideration the last notify order for date
        // * this for no request all notify
        // ? this notifications no override the notification from database
        val listConcatNotify = notifyDataSource.getLastNotifications(
            numberRequest = SIZE_NOTIFY_REQUEST,
            startWith = notifyDAO.getLastNotify()?.id
        )
        if (listConcatNotify.isNotEmpty()) notifyDAO.insertListNotify(listConcatNotify)
        return listConcatNotify.size
    }

    override suspend fun deleterAllNotify() =
        notifyDAO.deleterAll()

    override suspend fun openNotify(notify: Notify) {
        notifyDataSource.updateOpenNotify(notify.id)
        notifyDAO.updateNotify(notify.copy(isOpen = true))
    }

}