package com.nullpointer.blogcompose.services.uploadImg

import android.content.Context
import android.content.Intent
import android.net.Uri

object UploadDataControl {
    const val ACTION_START_POST = "ACTION_START_POST"
    const val ACTION_START_USER = "ACTION_START_USER"
    const val ACTION_STOP = "ACTION_STOP"
    const val KEY_IMG_SERVICES = "KEY_FILE_IMG_POST"
    const val KEY_DESC_POST_SERVICES = "KEY_DESC_POST_SERVICES"
    const val KEY_NAME_USER_SERVICES = "KEY_DESCRIPTION_POST"

    fun startServicesUploadPost(context: Context, description: String, uriImg: Uri) {
        Intent(context, UploadDataServices::class.java).also {
            it.putExtra(KEY_DESC_POST_SERVICES, description)
            it.putExtra(KEY_IMG_SERVICES, uriImg)
            it.action = ACTION_START_POST
            context.startService(it)
        }
    }

    fun startServicesUploadUser(context: Context, uriImg: Uri, nameUser: String? = null) {
        Intent(context, UploadDataServices::class.java).also {
            it.putExtra(KEY_NAME_USER_SERVICES, nameUser)
            it.putExtra(KEY_IMG_SERVICES, uriImg)
            it.action = ACTION_START_USER
            context.startService(it)
        }
    }

    fun stopServicesUploadPost(context: Context) {
        Intent(context, UploadDataServices::class.java).also {
            it.action = ACTION_STOP
            context.startService(it)
        }
    }
}