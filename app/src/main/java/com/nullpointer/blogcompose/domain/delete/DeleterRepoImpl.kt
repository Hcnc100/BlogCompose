package com.nullpointer.blogcompose.domain.delete

import com.nullpointer.blogcompose.data.local.preferences.PreferencesDataSource
import com.nullpointer.blogcompose.data.remote.notify.NotifyDataSource
import com.nullpointer.blogcompose.data.remote.post.PostRemoteDataSource

class DeleterRepoImpl(
    private val postDataSource: PostRemoteDataSource,
    private val notifyDataSource: NotifyDataSource,
    private val preferencesDataSource: PreferencesDataSource
) : DeleterRepository {
    override suspend fun clearAllData() {
        postDataSource
        notifyDataSource
        preferencesDataSource.deleterUser()
    }
}