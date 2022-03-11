package com.nullpointer.blogcompose.domain.notify

import com.nullpointer.blogcompose.core.utils.InternetCheck
import com.nullpointer.blogcompose.core.utils.NetworkException
import com.nullpointer.blogcompose.data.local.cache.NotifyDAO
import com.nullpointer.blogcompose.data.remote.NotifyDataSource
import com.nullpointer.blogcompose.models.Notify
import kotlinx.coroutines.flow.Flow
import timber.log.Timber

class NotifyRepoImpl(
    private val notifyDataSource: NotifyDataSource,
    private val notifyDAO: NotifyDAO,
) : NotifyRepository {
    companion object {
        private const val SIZE_NOTIFY_REQUEST = 10
    }

    override suspend fun requestLastNotify(): Int {
        if (!InternetCheck.isNetworkAvailable()) throw NetworkException()
        notifyDataSource.getLastNotifications(
            nNotify = SIZE_NOTIFY_REQUEST,
            beforeId = notifyDAO.getFirstNotify()?.id).let {
            if (it.isNotEmpty()) notifyDAO.updateAllNotify(it)
            return it.size
        }
    }

    override suspend fun concatenateNotify(): Int {
        if (!InternetCheck.isNetworkAvailable()) throw NetworkException()
        notifyDataSource.getLastNotifications(
            nNotify = SIZE_NOTIFY_REQUEST,
            afterId = notifyDAO.getLastNotify()?.id
        ).let {
            if (it.isNotEmpty()) notifyDAO.insertListNotify(it)

            return it.size
        }
    }

    override fun getAllNotifications(): Flow<List<Notify>> =
        notifyDAO.getAllNotify()

}