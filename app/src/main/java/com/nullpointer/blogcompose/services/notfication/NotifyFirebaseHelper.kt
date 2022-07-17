package com.nullpointer.blogcompose.services.notfication

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.net.toUri
import coil.ImageLoader
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import coil.transform.Transformation
import com.nullpointer.blogcompose.R
import com.nullpointer.blogcompose.core.utils.correctFlag
import com.nullpointer.blogcompose.core.utils.getNotifyServices
import com.nullpointer.blogcompose.core.utils.toFormat
import com.nullpointer.blogcompose.models.notify.Notify
import com.nullpointer.blogcompose.models.notify.TypeNotify
import com.nullpointer.blogcompose.models.notify.TypeNotify.COMMENT
import com.nullpointer.blogcompose.models.notify.TypeNotify.LIKE
import com.nullpointer.blogcompose.ui.activitys.MainActivity
import kotlin.random.Random

class NotifyFirebaseHelper(private val context: Context) {

    companion object {
        private const val ID_CHANNEL_POST_NOTIFY = "ID_CHANNEL_POST_NOTIFY"
    }

    private val notificationManager = context.getNotifyServices()


    init {
        createChannelNotification()
    }

    private fun createChannelNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                ID_CHANNEL_POST_NOTIFY,
                context.getString(R.string.name_channel_upload),
                NotificationManager.IMPORTANCE_DEFAULT,
            )
            notificationManager.createNotificationChannel(channel)
        }
    }


    private fun getBaseNotification(typeNotify: TypeNotify) =
        NotificationCompat.Builder(
            context,
            ID_CHANNEL_POST_NOTIFY
        ).setAutoCancel(true)
            .setOngoing(false).apply {
                when (typeNotify) {
                    LIKE -> {
                        setSmallIcon(R.drawable.ic_fav)
                        setContentTitle(context.getString(R.string.text_content_notify_like))
                    }
                    COMMENT -> {
                        setSmallIcon(R.drawable.ic_comment)
                        setContentTitle(context.getString(R.string.text_content_notify_comment))
                    }
                }
            }


    private fun getPendingIntentCompose(idPost: String): PendingIntent {
        // * create deep link
        // * this go to post for notification
        val deepLinkIntent = Intent(
            Intent.ACTION_VIEW,
            "https://www.blog-compose.com/post/$idPost".toUri(),
            context,
            MainActivity::class.java
        )
        // * create pending intent compose
        val deepLinkPendingIntent = TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(deepLinkIntent)
            getPendingIntent(0, context.correctFlag)
        }
        return deepLinkPendingIntent
    }

    private fun createCustomNotification(
        bitmapUser: Bitmap?,
        bitmapPost: Bitmap?,
        nameUserLiked: String,
        typeNotify: TypeNotify,
    ): RemoteViews {
        val (title, body) = when (typeNotify) {
            LIKE -> Pair(
                context.getString(R.string.text_title_notify_like),
                context.getString(R.string.message_notify_liked, nameUserLiked)
            )
            COMMENT -> Pair(
                context.getString(R.string.text_title_notify_comment),
                context.getString(R.string.message_notify_comment, nameUserLiked)
            )
        }
        return RemoteViews(context.packageName, R.layout.notify_liked).apply {
            setImageViewBitmap(R.id.img_user_liked, bitmapUser)
            setImageViewBitmap(R.id.img_post_liked, bitmapPost)
            setTextViewText(R.id.text_date_notify, System.currentTimeMillis().toFormat(context))
            setTextViewText(R.id.title, title)
            setTextViewText(R.id.text, body)
        }
    }


    suspend fun launchNotifyPost(notify: Notify) {
        val baseNotify = getBaseNotification(notify.type)
        // * create action to click on notification
        val deepLinkPendingIntent = getPendingIntentCompose(notify.idPost)
        // * create custom notification
        val customNotify = createCustomNotification(
            bitmapUser = getBitmapFromUrl(
                notify.userInNotify?.urlImg.toString(),
                CircleCropTransformation()
            ),
            bitmapPost = getBitmapFromUrl(notify.urlImgPost),
            nameUserLiked = notify.userInNotify?.name.toString(),
            typeNotify = notify.type
        )
        baseNotify.setContentIntent(deepLinkPendingIntent).setCustomContentView(customNotify)
        notificationManager.notify(Random.nextInt(), baseNotify.build())
    }

    private suspend fun getBitmapFromUrl(
        urlImg: String,
        vararg listTransformation: Transformation
    ): Bitmap? {
        val loader = ImageLoader(context)
        val request = ImageRequest.Builder(context)
            .data(urlImg)
            .allowHardware(false)
            .transformations(listTransformation.toList()).build()
        val result = loader.execute(request).drawable
        return result?.toBitmap()
    }

}