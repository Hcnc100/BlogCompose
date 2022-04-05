package com.nullpointer.blogcompose.services.uploadImg

import android.app.*
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import com.nullpointer.blogcompose.R
import com.nullpointer.blogcompose.core.utils.toFormat
import com.nullpointer.blogcompose.models.notify.TypeNotify
import com.nullpointer.blogcompose.models.notify.TypeNotify.*
import com.nullpointer.blogcompose.ui.activitys.MainActivity
import kotlin.random.Random

object NotificationChannelHelper {

     const val ID_CHANNEL_UPLOAD_POST = "ID_CHANNEL_UPLOAD_POST_BLOG_COMPOSE"
     const val NAME_UPLOAD_POST_CHANNEL = "Estado de subida del post"
     const val ID_CHANNEL_POST_NOTIFY = "ID_CHANNEL_POST_NOTIFY_BLOG_COMPOSE"
     const val NAME_CHANNEL_LIKE = "Notificaciones de los post"
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














