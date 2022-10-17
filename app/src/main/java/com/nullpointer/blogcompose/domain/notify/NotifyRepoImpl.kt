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
        val firstNotify = if (forceRefresh) null else notifyLocalDataSource.getFirstNotify()
        val listNewNotify = callApiTimeOut {
            notifyRemoteDataSource.getLastNotifications(
                numberRequest = SIZE_NOTIFY_REQUEST,
                idNotify = firstNotify?.id
            )
        }
        if (listNewNotify.isNotEmpty()) notifyLocalDataSource.updateAllNotify(listNewNotify)
        return listNewNotify.size
    }

    override suspend fun requestLastNotifyStartWith(idNotify: String) {
        val firstNotify = notifyLocalDataSource.getFirstNotify()
        val listNewNotify = callApiTimeOut {
            notifyRemoteDataSource.getLastPostBetween(
                startWithId = idNotify,
                endWithId = firstNotify?.id,
            )
        }
        notifyLocalDataSource.insertListNotify(listNewNotify)
    }

    override suspend fun concatenateNotify(): Int {
        val lastNotify = notifyLocalDataSource.getLastNotify()
        return if (lastNotify != null) {
            val listConcatNotify = callApiTimeOut {
                notifyRemoteDataSource.getConcatenateNotify(
                    numberRequest = SIZE_NOTIFY_REQUEST,
                    idNotify = lastNotify.id
                )
            }
            notifyLocalDataSource.insertListNotify(listConcatNotify)
            return listConcatNotify.size
        } else {
            0
        }
    }

    override suspend fun deleterAllNotify() =
        notifyLocalDataSource.deleterAllNotify()

    override suspend fun openNotify(notify: Notify) {
        callApiTimeOut {
            notifyRemoteDataSource.updateOpenNotify(notify.id)
        }
        notifyLocalDataSource.updateNotify(notify.copy(isOpen = true))
    }


}