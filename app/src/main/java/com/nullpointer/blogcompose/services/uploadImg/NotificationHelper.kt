package com.nullpointer.blogcompose.services.uploadImg

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.nullpointer.blogcompose.R

object NotificationChannelHelper {

     const val ID_CHANNEL_UPLOAD_POST = "ID_CHANNEL_UPLOAD_POST_BLOG_COMPOSE"
     const val NAME_UPLOAD_POST_CHANNEL = R.string.name_channel_upload_post
     const val ID_CHANNEL_POST_NOTIFY = "ID_CHANNEL_POST_NOTIFY_BLOG_COMPOSE"
     const val NAME_CHANNEL_LIKE = R.string.name_channel_notify_post
    const val ID_NOTIFY_UPLOAD=123456

    fun createChannelNotification(
        idNotificationChannel: String,
        nameNotificationChanel: String,
        importance: Int,
        context: Context,
    ): NotificationManager {
        // * get notification manager
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // * if is needed create channel notification
            val channel = NotificationChannel(
                idNotificationChannel,
                nameNotificationChanel,
                importance
            )
            notificationManager.createNotificationChannel(channel)
        }
        return notificationManager
    }

}














