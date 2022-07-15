package com.nullpointer.blogcompose.inject

import com.nullpointer.blogcompose.data.local.PreferencesDataSource
import com.nullpointer.blogcompose.data.remote.auth.AuthDataSourceImpl
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
    fun provideAuthDataSource(): AuthDataSourceImpl =
        AuthDataSourceImpl()

    @Provides
    @Singleton
    fun provideAuthRepository(
        authDataSource: AuthDataSourceImpl,
        preferencesDataSource: PreferencesDataSource,
        imagesDataSource: ImagesDataSourceImpl
    ): AuthRepoImpl = AuthRepoImpl(authDataSource, preferencesDataSource, imagesDataSource)


}