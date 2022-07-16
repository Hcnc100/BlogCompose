package com.nullpointer.blogcompose.inject

import com.nullpointer.blogcompose.data.remote.image.ImagesDataSource
import com.nullpointer.blogcompose.data.remote.image.ImagesDataSourceImpl
import com.nullpointer.blogcompose.domain.images.ImagesRepoImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ImageModule {

    @Provides
    @Singleton
    fun getImagesDataSource(): ImagesDataSource = ImagesDataSourceImpl()

    @Provides
    @Singleton
    fun getImagesRepository(
        imagesDataSource: ImagesDataSource,
    ): ImagesRepoImpl = ImagesRepoImpl(imagesDataSource)

}