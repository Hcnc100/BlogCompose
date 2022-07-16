package com.nullpointer.blogcompose.inject

import com.nullpointer.blogcompose.data.local.preferences.PreferencesDataSource
import com.nullpointer.blogcompose.data.local.preferences.PreferencesDataSourceImpl
import com.nullpointer.blogcompose.data.remote.auth.AuthDataSource
import com.nullpointer.blogcompose.data.remote.auth.AuthDataSourceImpl
import com.nullpointer.blogcompose.data.remote.image.ImagesDataSource
import com.nullpointer.blogcompose.data.remote.image.ImagesDataSourceImpl
import com.nullpointer.blogcompose.domain.auth.AuthRepoImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Provides
    @Singleton
    fun provideAuthDataSource(): AuthDataSource =
        AuthDataSourceImpl()

    @Provides
    @Singleton
    fun provideAuthRepository(
        authDataSource: AuthDataSource,
        preferencesDataSource: PreferencesDataSource,
        imagesDataSource: ImagesDataSource
    ): AuthRepoImpl = AuthRepoImpl(authDataSource, preferencesDataSource, imagesDataSource)


}