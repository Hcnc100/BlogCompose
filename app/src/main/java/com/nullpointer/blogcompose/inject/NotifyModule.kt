package com.nullpointer.blogcompose.inject

import com.nullpointer.blogcompose.data.local.cache.BlogDataBase
import com.nullpointer.blogcompose.data.local.cache.NotifyDAO
import com.nullpointer.blogcompose.data.local.notify.NotifyLocalDataSource
import com.nullpointer.blogcompose.data.local.notify.NotifyLocalDataSourceImpl
import com.nullpointer.blogcompose.data.remote.notify.NotifyRemoteDataSource
import com.nullpointer.blogcompose.data.remote.notify.NotifyRemoteDataSourceImpl
import com.nullpointer.blogcompose.domain.notify.NotifyRepoImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NotifyModule {

    @Provides
    @Singleton
    fun provideNotifyDao(
        database: BlogDataBase,
    ): NotifyDAO = database.getNotifyDAO()

    @Provides
    @Singleton
    fun provideNotifyRemoteDataSource(): NotifyRemoteDataSource =
        NotifyRemoteDataSourceImpl()

    @Provides
    @Singleton
    fun provideNotifyLocalDataSource(
        notifyDAO: NotifyDAO
    ): NotifyLocalDataSource = NotifyLocalDataSourceImpl(notifyDAO)

    @Provides
    @Singleton
    fun provideNotifyRepo(
        notifyRemoteDataSource: NotifyRemoteDataSource,
        notifyLocalDataSource: NotifyLocalDataSource
    ): NotifyRepoImpl = NotifyRepoImpl(notifyLocalDataSource, notifyRemoteDataSource)
}