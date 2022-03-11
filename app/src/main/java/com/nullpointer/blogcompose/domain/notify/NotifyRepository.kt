package com.nullpointer.blogcompose.domain.notify

import com.nullpointer.blogcompose.models.Notify
import kotlinx.coroutines.flow.Flow

interface NotifyRepository {
    val listNotify: Flow<List<Notify>>
    suspend fun requestLastNotify(forceRefresh: Boolean = false): Int
    suspend fun concatenateNotify(): Int
}