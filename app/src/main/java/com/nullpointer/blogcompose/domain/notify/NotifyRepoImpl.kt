package com.nullpointer.blogcompose.domain.notify

import com.nullpointer.blogcompose.core.utils.InternetCheck
import com.nullpointer.blogcompose.data.local.cache.NotifyDAO
import com.nullpointer.blogcompose.data.remote.NotifyDataSource
import com.nullpointer.blogcompose.domain.preferences.PreferencesRepoImpl
import com.nullpointer.blogcompose.models.Notify
import kotlinx.coroutines.flow.Flow
import timber.log.Timber

class NotifyRepoImpl(
    private val notifyDataSource: NotifyDataSource,
    private val notifyDAO: NotifyDAO,
) : NotifyRepository {
    companion object {
        private const val SIZE_CACHING = 20
    }

    override suspend fun getNotify(inCaching: Boolean): List<Notify> {
        return if (!inCaching && InternetCheck.isNetworkAvailable()) {
            notifyDataSource.getListNotify().also {
                Timber.d("Notificaciones obtenidas del server ${it.size}")
                val listSave = if (it.size < SIZE_CACHING) it else it.subList(0, SIZE_CACHING)
                notifyDAO.deleterAll()
                notifyDAO.insertListNotify(listSave)
            }
        } else {
            Timber.d("Notificaciones obtenidas del cache")
            notifyDAO.getAllNotify()
        }

    }

}