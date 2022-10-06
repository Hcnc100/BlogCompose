package com.nullpointer.blogcompose.domain.delete

import com.nullpointer.blogcompose.data.local.post.PostLocalDataSource
import com.nullpointer.blogcompose.data.local.preferences.PreferencesDataSource

class DeleterRepoImpl(
    private val postLocalDataSource: PostLocalDataSource,
    private val preferencesDataSource: PreferencesDataSource
) : DeleterRepository {
    override suspend fun clearAllData() {
        postLocalDataSource.deleterAllPost()
        preferencesDataSource.deleterUser()
    }
}