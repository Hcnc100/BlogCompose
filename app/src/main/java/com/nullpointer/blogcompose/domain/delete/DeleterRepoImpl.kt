package com.nullpointer.blogcompose.domain.delete

import com.nullpointer.blogcompose.data.local.notify.NotifyLocalDataSource
import com.nullpointer.blogcompose.data.local.post.PostLocalDataSource
import com.nullpointer.blogcompose.data.local.preferences.PreferencesDataSource

class DeleterRepoImpl(
    private val postLocalDataSource: PostLocalDataSource,
    private val notifyLocalDataSource: NotifyLocalDataSource,
    private val preferencesDataSource: PreferencesDataSource
) : DeleterRepository {
    override suspend fun clearAllData() {
        preferencesDataSource.deleterData()
        postLocalDataSource.deleterAllPost()
        notifyLocalDataSource.deleterAllNotify()
    }
}