package com.nullpointer.blogcompose.domain.images

import android.net.Uri
import com.nullpointer.blogcompose.core.states.StorageUploadTaskResult
import com.nullpointer.blogcompose.data.remote.ImagesDataSource
import kotlinx.coroutines.flow.Flow

class ImagesRepoImpl(
    private val imagesDataSource: ImagesDataSource
):ImagesRepository {
    override fun uploadImgProfile(uri: Uri): Flow<StorageUploadTaskResult> {
        TODO("Not yet implemented")
    }

    override fun uploadImgBlog(uri: Uri,name:String): Flow<StorageUploadTaskResult> =
        imagesDataSource.uploadImagePost(uri, name)
}