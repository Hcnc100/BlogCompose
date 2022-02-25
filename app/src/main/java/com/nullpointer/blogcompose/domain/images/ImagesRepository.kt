package com.nullpointer.blogcompose.domain.images

import android.net.Uri
import com.nullpointer.blogcompose.core.states.StorageUploadTaskResult
import kotlinx.coroutines.flow.Flow

interface ImagesRepository {

    fun uploadImgProfile(uri: Uri):Flow<StorageUploadTaskResult>

    fun uploadImgBlog(uri: Uri,name:String):Flow<StorageUploadTaskResult>



}