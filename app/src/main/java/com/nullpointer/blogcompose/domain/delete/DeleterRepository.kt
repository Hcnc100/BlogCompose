package com.nullpointer.blogcompose.domain.delete

interface DeleterRepository {
    suspend fun clearAllData()
}