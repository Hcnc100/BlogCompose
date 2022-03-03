package com.nullpointer.blogcompose.services.uploadImg

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.nullpointer.blogcompose.R
import com.nullpointer.blogcompose.ui.activitys.MainActivity

class NotificationHelper(context: Context) : ContextWrapper(context) {

    fun getNotificationUploadServices(
        idNotificationChannel: String,
        nameNotificationChanel: String,
        importance: Int,
        nameActionStop: String
    ): NotificationCompat.Builder {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                idNotificationChannel,
                nameNotificationChanel,
                importance
            )
            notificationManager.createNotificationChannel(channel)
        }
        return getNotificationUpload(idNotificationChannel, nameActionStop)
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

    private fun getPendingIntentStop(nameActionStop:String): PendingIntent =
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