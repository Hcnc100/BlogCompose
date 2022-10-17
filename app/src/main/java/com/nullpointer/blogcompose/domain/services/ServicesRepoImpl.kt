package com.nullpointer.blogcompose.domain.services

import android.net.Uri

interface ServicesRepository {

    fun startUploadImgProfile(newImg: Uri)
    fun startUploadPost(descriptionPost: String, imagePost: Uri)
}