package com.nullpointer.blogcompose.inject

import com.nullpointer.blogcompose.data.local.cache.BlogDataBase
import com.nullpointer.blogcompose.data.local.cache.NotifyDAO
import com.nullpointer.blogcompose.data.remote.notify.NotifyDataSource
import com.nullpointer.blogcompose.data.remote.notify.NotifyDataSourceImpl
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
    fun provideNotifyDatSource(): NotifyDataSource =
        NotifyDataSourceImpl()

    @Provides
    @Singleton
    fun provideNotifyRepo(
        notifyDataSource: NotifyDataSource,
        notifyDAO: NotifyDAO,
    ): NotifyRepoImpl = NotifyRepoImpl(notifyDataSource, notifyDAO)
}