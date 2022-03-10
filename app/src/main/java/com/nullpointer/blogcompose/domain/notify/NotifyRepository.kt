package com.nullpointer.blogcompose.domain.notify

import com.nullpointer.blogcompose.models.Notify
import kotlinx.coroutines.flow.Flow

interface NotifyRepository {
    suspend fun requestLastNotify(): Int
    suspend fun concatenateNotify(): Int
    fun getAllNotifications(): Flow<List<Notify>>
}