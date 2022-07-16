package com.nullpointer.blogcompose.inject

import com.nullpointer.blogcompose.data.local.preferences.PreferencesDataSource
import com.nullpointer.blogcompose.data.remote.comment.CommentsDataSource
import com.nullpointer.blogcompose.data.remote.comment.CommentsDataSourceImpl
import com.nullpointer.blogcompose.domain.comment.CommentsRepoImpl
import com.nullpointer.blogcompose.domain.comment.CommentsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CommentsModule {

    @Provides
    @Singleton
    fun provideCommentsDataSource():CommentsDataSource=
        CommentsDataSourceImpl()

    @Provides
    @Singleton
    fun provideCommentsRepository(
        preferencesDataSource: PreferencesDataSource,
        commentsDataSource: CommentsDataSource
    ): CommentsRepoImpl= CommentsRepoImpl(preferencesDataSource,commentsDataSource)
}