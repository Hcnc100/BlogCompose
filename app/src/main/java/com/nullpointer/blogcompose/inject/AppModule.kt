package com.nullpointer.blogcompose.inject

import com.nullpointer.blogcompose.data.remote.AuthDataSource
import com.nullpointer.blogcompose.data.remote.ImagesDataSource
import com.nullpointer.blogcompose.data.remote.PostDataSource
import com.nullpointer.blogcompose.domain.auth.AuthRepoImpl
import com.nullpointer.blogcompose.domain.auth.AuthRepository
import com.nullpointer.blogcompose.domain.images.ImagesRepoImpl
import com.nullpointer.blogcompose.domain.post.PostRepoImpl
import com.nullpointer.blogcompose.domain.post.PostRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun getPostDataSource(): PostDataSource =
        PostDataSource()

    @Provides
    @Singleton
    fun getPostRepository(
        postDataSource: PostDataSource,
    ): PostRepoImpl = PostRepoImpl(postDataSource)


    @Provides
    @Singleton
    fun getImagesDataSource(): ImagesDataSource =
        ImagesDataSource()

    @Provides
    @Singleton
    fun getImagesRepository(
        imagesDataSource: ImagesDataSource,
    ): ImagesRepoImpl = ImagesRepoImpl(imagesDataSource)

    @Provides
    @Singleton
    fun provideAuthDataSource(): AuthDataSource =
        AuthDataSource()

    @Provides
    @Singleton
    fun provideAuthRepository(
        authDataSource: AuthDataSource,
    ): AuthRepoImpl = AuthRepoImpl(authDataSource)
}