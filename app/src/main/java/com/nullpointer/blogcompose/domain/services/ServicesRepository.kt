package com.nullpointer.blogcompose.domain.services

import android.content.Context
import android.net.Uri
import com.nullpointer.blogcompose.services.uploadImg.UploadDataControl

class ServicesRepositoryImpl(
    private val context: Context
) : ServicesRepository {

    override fun startUploadImgProfile(newImg: Uri) {
        UploadDataControl.startServicesUploadUser(
            context = context,
            uriImg = newImg
        )
    }

    override fun startUploadPost(descriptionPost: String, imagePost: Uri) {
        UploadDataControl.startServicesUploadPost(
            context = context,
            uriImg = imagePost,
            description = descriptionPost
        )
    }
}