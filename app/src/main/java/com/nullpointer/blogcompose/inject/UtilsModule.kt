package com.nullpointer.blogcompose.inject

import android.content.Context
import com.nullpointer.blogcompose.data.local.notify.NotifyLocalDataSource
import com.nullpointer.blogcompose.data.local.post.PostLocalDataSource
import com.nullpointer.blogcompose.data.local.preferences.PreferencesDataSource
import com.nullpointer.blogcompose.domain.compress.CompressRepoImpl
import com.nullpointer.blogcompose.domain.delete.DeleterRepoImpl
import com.nullpointer.blogcompose.domain.services.ServicesRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UtilsModule {

    @Provides
    @Singleton
    fun provideServicesRepository(
        @ApplicationContext context: Context
    ): ServicesRepositoryImpl = ServicesRepositoryImpl(context)

    @Provides
    @Singleton
    fun provideCompressRepository(
        @ApplicationContext context: Context
    ): CompressRepoImpl = CompressRepoImpl(context)

    @Provides
    @Singleton
    fun provideDeleterRepository(
        postLocalDataSource: PostLocalDataSource,
        notifyLocalDataSource: NotifyLocalDataSource,
        preferencesDataSource: PreferencesDataSource
    ): DeleterRepoImpl = DeleterRepoImpl(
        postLocalDataSource,
        notifyLocalDataSource,
        preferencesDataSource
    )
}