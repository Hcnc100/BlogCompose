package com.nullpointer.blogcompose.services.uploadImg

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.nullpointer.blogcompose.R
import com.nullpointer.blogcompose.core.utils.toFormat
import com.nullpointer.blogcompose.ui.activitys.MainActivity
import kotlin.random.Random

class NotificationHelper(context: Context) : ContextWrapper(context) {
    companion object {
        private const val ID_CHANNEL_UPLOAD_POST = "ID_CHANNEL_UPLOAD_POST"
        private const val NAME_UPLOAD_POST_CHANNEL = "UPLOAD_POST_CHANNEL"
        private const val ID_CHANNEL_LIKE = "ID_CHANNEL_LIKE"
        private const val NAME_CHANNEL_LIKE = "NAME_CHANNEL_LIKE"
    }

    fun getNotifyUploadServices(
        nameActionStop: String,
    ): NotificationCompat.Builder {
        createChannel(
            ID_CHANNEL_UPLOAD_POST,
            NAME_UPLOAD_POST_CHANNEL,
            NotificationManagerCompat.IMPORTANCE_DEFAULT,
        )
        return getNotificationUpload(ID_CHANNEL_UPLOAD_POST, nameActionStop)

    }

    fun launchNotifyLike(
        bitmapPost: Bitmap,
        bitmapUser: Bitmap,
        nameUserLiked: String,
        idPost: String,
    ) {
        val notificationManager = createChannel(
            ID_CHANNEL_LIKE,
            NAME_CHANNEL_LIKE,
            NotificationManagerCompat.IMPORTANCE_HIGH
        )
        val baseNotify = getBaseNotificationLiked()
        val completeNotify =
            createCustomNotification(bitmapUser, bitmapPost, nameUserLiked, baseNotify)
        notificationManager.notify(Random.nextInt(), completeNotify)
    }

    private fun createCustomNotification(
        bitmapUser: Bitmap,
        bitmapPost: Bitmap,
        nameUserLiked: String,
        baseNotify: NotificationCompat.Builder,
    ): Notification {
        val remoteViews = RemoteViews(this.packageName, R.layout.notify_liked)
        remoteViews.setImageViewBitmap(R.id.img_user_liked, bitmapUser)
        remoteViews.setImageViewBitmap(R.id.img_post_liked, bitmapPost)
        remoteViews.setTextViewText(R.id.text_date_notify,
            System.currentTimeMillis().toFormat(this))
        remoteViews.setTextViewText(R.id.title, "Recibiste un like")
        remoteViews.setTextViewText(R.id.text, "A $nameUserLiked le gusta tu post")

        baseNotify.setCustomContentView(remoteViews)

        return baseNotify.build()
    }

    private fun getBaseNotificationLiked() =
        NotificationCompat.Builder(this, ID_CHANNEL_LIKE)
            .setAutoCancel(true)
            .setOngoing(false)
            .setSmallIcon(R.drawable.ic_fav)
            .setContentTitle("pan")

    private fun createChannel(
        idNotificationChannel: String,
        nameNotificationChanel: String,
        importance: Int,
    ): NotificationManager {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                idNotificationChannel,
                nameNotificationChanel,
                importance
            )
            notificationManager.createNotificationChannel(channel)
        }
        return notificationManager
    }

    private fun getPendingIntentToClick(
        actionClickNotification: String,
    ): PendingIntent =
        PendingIntent.getActivity(
            this, 0, Intent(this, MainActivity::class.java).also {
                it.action = actionClickNotification
            },
            PendingIntent.FLAG_UPDATE_CURRENT
        )

    private fun getPendingIntentStop(nameActionStop: String): PendingIntent =
        PendingIntent.getService(this, 1,
            Intent(this, this::class.java).apply {
                action = nameActionStop
            }, PendingIntent.FLAG_UPDATE_CURRENT
        )


    private fun getNotificationUpload(idNotificationChannel: String, nameActionStop: String) =
        NotificationCompat.Builder(this, idNotificationChannel)
            .setAutoCancel(false)
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_upload)
            .setContentTitle("Subiendo...")
            .setContentIntent(getPendingIntentToClick(nameActionStop))
            .addAction(R.drawable.ic_stop, "Stop", getPendingIntentStop(nameActionStop))
}