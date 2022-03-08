package com.nullpointer.blogcompose.inject

import android.content.Context
import androidx.room.Room
import com.nullpointer.blogcompose.data.local.PreferencesDataSource
import com.nullpointer.blogcompose.data.local.cache.BlogDataBase
import com.nullpointer.blogcompose.data.local.cache.BlogDataBase.Companion.BLOG_DATABASE
import com.nullpointer.blogcompose.data.local.cache.NotifyDAO
import com.nullpointer.blogcompose.data.local.cache.PostDAO
import com.nullpointer.blogcompose.data.remote.*
import com.nullpointer.blogcompose.domain.auth.AuthRepoImpl
import com.nullpointer.blogcompose.domain.auth.AuthRepository
import com.nullpointer.blogcompose.domain.images.ImagesRepoImpl
import com.nullpointer.blogcompose.domain.notify.NotifyRepoImpl
import com.nullpointer.blogcompose.domain.post.PostRepoImpl
import com.nullpointer.blogcompose.domain.post.PostRepository
import com.nullpointer.blogcompose.domain.preferences.PreferencesRepoImpl
import com.nullpointer.blogcompose.domain.toke.TokenRepoImpl
import com.nullpointer.blogcompose.models.Post
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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

    @Provides
    @Singleton
    fun provideTokenDataSource(): TokenDataSource =
        TokenDataSource()

    @Provides
    @Singleton
    fun tokenRepository(tokenDataSource: TokenDataSource): TokenRepoImpl =
        TokenRepoImpl(tokenDataSource)

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
    fun provideNotifyDatSource(): NotifyDataSource =
        NotifyDataSource()

    @Provides
    @Singleton
    fun provideNotifyRepo(notifyDataSource: NotifyDataSource):
            NotifyRepoImpl = NotifyRepoImpl(notifyDataSource)

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext app: Context,
    ): BlogDataBase = Room.databaseBuilder(
        app,
        BlogDataBase::class.java,
        BLOG_DATABASE
    ).build()

    @Provides
    @Singleton
    fun getNotifyDao(
        databse: BlogDataBase,
    ): NotifyDAO = databse.getNotifyDAO()

    @Provides
    @Singleton
    fun getPostDao(
        database: BlogDataBase,
    ): PostDAO = database.getPostDAO()

}