package com.nullpointer.blogcompose.domain.services

import android.content.Context
import android.net.Uri
import com.nullpointer.blogcompose.services.uploadImg.UploadDataControl
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

class ServicesRepositoryImpl(
    private val context: Context
) : ServicesRepository {

    private val _finishUploadSuccessEvent = Channel<Unit>()
    override val finishUploadSuccessEvent = _finishUploadSuccessEvent.receiveAsFlow()


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

    override fun notifyPostSuccessUpload() {
        _finishUploadSuccessEvent.trySend(Unit)
    }
}