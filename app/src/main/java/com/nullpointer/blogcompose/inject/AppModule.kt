package com.nullpointer.blogcompose.inject

import com.nullpointer.blogcompose.data.remote.PostDataSource
import com.nullpointer.blogcompose.domain.post.PostRepoImpl
import com.nullpointer.blogcompose.domain.post.PostRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    @Singleton
    fun getPostDataSource(): PostDataSource =
        PostDataSource()

    @Provides
    @Singleton
    fun getPostRepository(
        postDataSource: PostDataSource,
    ): PostRepoImpl =
        PostRepoImpl(postDataSource)
}