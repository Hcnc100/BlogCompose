package com.nullpointer.blogcompose.inject

import android.content.Context
import androidx.room.Room
import com.nullpointer.blogcompose.data.local.PreferencesDataSource
import com.nullpointer.blogcompose.data.local.cache.*
import com.nullpointer.blogcompose.data.local.cache.BlogDataBase.Companion.BLOG_DATABASE
import com.nullpointer.blogcompose.data.remote.*
import com.nullpointer.blogcompose.domain.auth.AuthRepoImpl
import com.nullpointer.blogcompose.domain.images.ImagesRepoImpl
import com.nullpointer.blogcompose.domain.notify.NotifyRepoImpl
import com.nullpointer.blogcompose.domain.post.PostRepoImpl
import com.nullpointer.blogcompose.domain.preferences.PreferencesRepoImpl
import com.nullpointer.blogcompose.domain.toke.TokenRepoImpl
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
        postDAO: PostDAO,
        myPostDAO: MyPostDAO,
        commentsDAO: CommentsDAO,
    ): PostRepoImpl = PostRepoImpl(postDataSource, postDAO, myPostDAO, commentsDAO)


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
        preferencesDataSource: PreferencesDataSource,
    ): AuthRepoImpl = AuthRepoImpl(authDataSource, preferencesDataSource)

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
    fun provideNotifyRepo(
        notifyDataSource: NotifyDataSource,
        notifyDAO: NotifyDAO,
    ): NotifyRepoImpl = NotifyRepoImpl(notifyDataSource, notifyDAO)

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
        database: BlogDataBase,
    ): NotifyDAO = database.getNotifyDAO()

    @Provides
    @Singleton
    fun getPostDao(
        database: BlogDataBase,
    ): PostDAO = database.getPostDAO()

    @Provides
    @Singleton
    fun getMyPost(
        database: BlogDataBase,
    ): MyPostDAO = database.getMyPostDAO()

    @Provides
    @Singleton
    fun getMCommentDao(
        database: BlogDataBase,
    ): CommentsDAO = database.getCommentsDAO()
}