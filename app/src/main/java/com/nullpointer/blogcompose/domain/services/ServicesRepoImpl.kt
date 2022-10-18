package com.nullpointer.blogcompose.domain.services

import android.net.Uri
import kotlinx.coroutines.flow.Flow

interface ServicesRepository {
    val eventHasNewPost: Flow<Unit>

    fun startUploadImgProfile(newImg: Uri)
    fun startUploadPost(descriptionPost: String, imagePost: Uri)
    fun notifyEventHasNewPost()
}