package com.nullpointer.blogcompose.inject

import android.content.Context
import androidx.room.Room
import com.nullpointer.blogcompose.data.local.PreferencesDataSource
import com.nullpointer.blogcompose.data.local.cache.BlogDataBase
import com.nullpointer.blogcompose.domain.preferences.PreferencesRepoImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PreferencesModule {

    @Provides
    @Singleton
    fun providePreferencesDataSource(
        @ApplicationContext context: Context,
    ): PreferencesDataSource =
        PreferencesDataSource(context)

    @Provides
    @Singleton
    fun providePreferencesRepo(
        preferencesDataSource: PreferencesDataSource,
    ): PreferencesRepoImpl = PreferencesRepoImpl(preferencesDataSource)

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext app: Context,
    ): BlogDataBase = Room.databaseBuilder(
        app,
        BlogDataBase::class.java,
        BlogDataBase.BLOG_DATABASE
    ).build()

}