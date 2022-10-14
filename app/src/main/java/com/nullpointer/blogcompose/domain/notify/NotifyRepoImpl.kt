package com.nullpointer.blogcompose.domain.notify

import com.nullpointer.blogcompose.core.utils.callApiTimeOut
import com.nullpointer.blogcompose.data.local.notify.NotifyLocalDataSource
import com.nullpointer.blogcompose.data.remote.notify.NotifyRemoteDataSource
import com.nullpointer.blogcompose.models.notify.Notify
import kotlinx.coroutines.flow.Flow

class NotifyRepoImpl(
    private val notifyLocalDataSource: NotifyLocalDataSource,
    private val notifyRemoteDataSource: NotifyRemoteDataSource
) : NotifyRepository {

    companion object {
        private const val SIZE_NOTIFY_REQUEST = 10L
    }

    override val listNotify: Flow<List<Notify>> = notifyLocalDataSource.listNotify

    override suspend fun requestLastNotify(forceRefresh: Boolean): Int {
        return callApiTimeOut {
            val firstNotify = if (forceRefresh) null else notifyLocalDataSource.getFirstNotify()
            val listNewNotify = notifyRemoteDataSource.getLastNotifications(
                numberRequest = SIZE_NOTIFY_REQUEST,
                idNotify = firstNotify?.id
            )
            if (listNewNotify.isNotEmpty()) notifyLocalDataSource.updateAllNotify(listNewNotify)
            listNewNotify.size
        }
    }

    override suspend fun requestLastNotifyStartWith(idNotify: String) {
        callApiTimeOut {
            val listConcatNotify = notifyRemoteDataSource.getLastNotifications(
                numberRequest = SIZE_NOTIFY_REQUEST,
                idNotify = idNotify,
                includeNotify = true
            )
            if (listConcatNotify.isNotEmpty()) notifyLocalDataSource.updateAllNotify(
                listConcatNotify
            )
        }
    }

    override suspend fun concatenateNotify(): Int {
        return callApiTimeOut {
            val listConcatNotify = notifyRemoteDataSource.getLastNotifications(
                numberRequest = SIZE_NOTIFY_REQUEST,
                idNotify = notifyLocalDataSource.getLastNotify()?.id
            )
            if (listConcatNotify.isNotEmpty()) notifyLocalDataSource.insertListNotify(
                listConcatNotify
            )
            listConcatNotify.size
        }
    }

    override suspend fun deleterAllNotify() =
        notifyLocalDataSource.deleterAllNotify()

    override suspend fun openNotify(notify: Notify) {
        callApiTimeOut {
            notifyRemoteDataSource.updateOpenNotify(notify.id)
            notifyLocalDataSource.updateNotify(notify.copy(isOpen = true))
        }
    }

}