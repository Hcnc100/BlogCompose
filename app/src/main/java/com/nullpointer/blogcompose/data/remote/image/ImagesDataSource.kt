package com.nullpointer.blogcompose.data.remote.image

import android.net.Uri
import com.nullpointer.blogcompose.core.states.StorageUploadTaskResult
import kotlinx.coroutines.flow.Flow

interface ImagesDataSource {
    fun uploadImageUserWithState(uriImg: Uri): Flow<StorageUploadTaskResult>
    fun uploadImagePostWithState(uriImg: Uri, name: String): Flow<StorageUploadTaskResult>
    suspend fun uploadImageUserWithOutState(uriImg: Uri):Uri
}