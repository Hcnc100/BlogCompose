package com.nullpointer.blogcompose.services.uploadImg

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.nullpointer.blogcompose.R
import com.nullpointer.blogcompose.core.utils.correctFlag
import com.nullpointer.blogcompose.core.utils.getNotifyServices
import com.nullpointer.blogcompose.ui.activitys.MainActivity

class NotifyUploadImgHelper(private val context: Context) {
    companion object {
        const val ID_CHANNEL_UPLOAD_DATA = "ID_CHANNEL_UPLOAD_POST_BLOG_COMPOSE"
        const val ID_NOTIFY_UPLOAD=123456
    }

    private val notificationManager = context.getNotifyServices()
    private val baseNotifyUpload by lazy { getNotificationUpload() }

    init {
        createChannelNotification()
    }

    private fun createChannelNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // * if is needed create channel notification
            val channel = NotificationChannel(
                ID_CHANNEL_UPLOAD_DATA,
                context.getString(R.string.name_channel_upload),
                NotificationManager.IMPORTANCE_DEFAULT,
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun getPendingIntentStop(): PendingIntent =
        PendingIntent.getService(
            context,
            1,
            Intent(context, UploadDataServices::class.java).apply { action =
                UploadDataControl.ACTION_STOP
            },
            context.correctFlag
        )

    private fun getPendingIntentToClick(): PendingIntent = PendingIntent.getActivity(
        context,
        0,
        Intent(context, MainActivity::class.java),
        context.correctFlag
    )

    private fun getNotificationUpload() =
        NotificationCompat.Builder(context, ID_CHANNEL_UPLOAD_DATA)
            .setOnlyAlertOnce(true)
            .setAutoCancel(false)
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_person)
            .setContentTitle(context.getString(R.string.title_upload_notify))
            .setContentIntent(getPendingIntentToClick())
            .addAction(R.drawable.ic_stop,
                context.getString(R.string.name_Action_stop),
                getPendingIntentStop())

    fun startServicesForeground(service: Service){
        service.startForeground(ID_NOTIFY_UPLOAD,baseNotifyUpload.build())
    }

    fun updateNotifyFinishUpdate() {
        baseNotifyUpload.setProgress(0,0,true)
        notificationManager.notify(ID_NOTIFY_UPLOAD, baseNotifyUpload.build())
    }

    fun updateNotifyProgressUpload(progress: Int){
        baseNotifyUpload.setProgress(100,progress,false)
        notificationManager.notify(ID_NOTIFY_UPLOAD, baseNotifyUpload.build())
    }

}













