package com.nullpointer.blogcompose.domain.images

import android.net.Uri
import com.nullpointer.blogcompose.core.states.StorageUploadTaskResult
import com.nullpointer.blogcompose.data.remote.image.ImagesDataSourceImpl
import kotlinx.coroutines.flow.Flow

class ImagesRepoImpl(
    private val imagesDataSource: ImagesDataSourceImpl,
) : ImagesRepository {
    override fun uploadImgProfile(uri: Uri): Flow<StorageUploadTaskResult> =
        imagesDataSource.uploadImageUserWithState(uri)

    override fun uploadImgBlog(uri: Uri, name: String): Flow<StorageUploadTaskResult> =
        imagesDataSource.uploadImagePostWithState(uri, name)
}