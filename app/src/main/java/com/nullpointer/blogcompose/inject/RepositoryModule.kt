package com.nullpointer.blogcompose.inject

import com.nullpointer.blogcompose.data.remote.notify.NotifyDataSource
import com.nullpointer.blogcompose.data.remote.notify.NotifyDataSourceImpl
import com.nullpointer.blogcompose.domain.auth.AuthRepoImpl
import com.nullpointer.blogcompose.domain.auth.AuthRepository
import com.nullpointer.blogcompose.domain.comment.CommentsRepoImpl
import com.nullpointer.blogcompose.domain.comment.CommentsRepository
import com.nullpointer.blogcompose.domain.images.ImagesRepoImpl
import com.nullpointer.blogcompose.domain.images.ImagesRepository
import com.nullpointer.blogcompose.domain.notify.NotifyRepoImpl
import com.nullpointer.blogcompose.domain.notify.NotifyRepository
import com.nullpointer.blogcompose.domain.post.PostRepoImpl
import com.nullpointer.blogcompose.domain.post.PostRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun provideAuthRepository(
        authRepoImpl: AuthRepoImpl
    ):AuthRepository

    @Binds
    @Singleton
    abstract fun provideImageRepository(
        imageRepoImpl: ImagesRepoImpl
    ):ImagesRepository

    @Binds
    @Singleton
    abstract fun provideNotifyRepository(
        notifyRepoImpl: NotifyRepoImpl
    ):NotifyRepository

    @Binds
    @Singleton
    abstract fun providePostRepository(
        postRepoImpl: PostRepoImpl
    ):PostRepository

    @Binds
    @Singleton
    abstract fun provideCommentsRepository(
        commentsRepoImpl: CommentsRepoImpl
    ):CommentsRepository
}