package com.nullpointer.blogcompose.inject

import com.nullpointer.blogcompose.data.local.cache.BlogDataBase
import com.nullpointer.blogcompose.data.local.cache.MyPostDAO
import com.nullpointer.blogcompose.data.local.cache.PostDAO
import com.nullpointer.blogcompose.data.local.preferences.PreferencesDataSource
import com.nullpointer.blogcompose.data.remote.post.PostDataSource
import com.nullpointer.blogcompose.data.remote.post.PostDataSourceImpl
import com.nullpointer.blogcompose.domain.post.PostRepoImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PostModule {

    @Provides
    @Singleton
    fun provideMyPostDao(
        database: BlogDataBase,
    ): MyPostDAO = database.getMyPostDAO()


    @Provides
    @Singleton
    fun getPostDao(
        database: BlogDataBase,
    ): PostDAO = database.getPostDAO()

    @Provides
    @Singleton
    fun getPostDataSource(): PostDataSource =
        PostDataSourceImpl()

    @Provides
    @Singleton
    fun getPostRepository(
        prefDataSource: PreferencesDataSource,
        postDataSource: PostDataSource,
        myPostDAO: MyPostDAO,
        postDAO: PostDAO,
    ): PostRepoImpl = PostRepoImpl(postDataSource, prefDataSource, postDAO, myPostDAO)

}