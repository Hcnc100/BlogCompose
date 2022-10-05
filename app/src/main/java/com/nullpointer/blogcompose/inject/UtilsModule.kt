package com.nullpointer.blogcompose.inject

import android.content.Context
import com.nullpointer.blogcompose.domain.compress.CompressRepoImpl
import com.nullpointer.blogcompose.domain.compress.CompressRepository
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
    fun provideCompressRepository(
        @ApplicationContext context: Context
    ):CompressRepoImpl= CompressRepoImpl(context)
}