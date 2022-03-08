package com.nullpointer.blogcompose.domain.notify

import com.nullpointer.blogcompose.models.Notify
import kotlinx.coroutines.flow.Flow

interface NotifyRepository {
    suspend fun getNotify(inCaching:Boolean): List<Notify>
}