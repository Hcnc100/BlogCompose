package com.nullpointer.blogcompose.domain.notify

import com.nullpointer.blogcompose.data.remote.NotifyDataSource
import com.nullpointer.blogcompose.models.Notify
import kotlinx.coroutines.flow.Flow

class NotifyRepoImpl(
    private val notifyDataSource: NotifyDataSource
):NotifyRepository {
    override suspend fun getNotify(): Flow<List<Notify>> =
        notifyDataSource.getListNotify()
}