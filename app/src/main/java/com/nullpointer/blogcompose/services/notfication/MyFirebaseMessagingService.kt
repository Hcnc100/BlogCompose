package com.nullpointer.blogcompose.services.notfication

import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Intent
import android.graphics.Bitmap
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.net.toUri
import coil.ImageLoader
import coil.annotation.ExperimentalCoilApi
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.GsonBuilder
import com.nullpointer.blogcompose.R
import com.nullpointer.blogcompose.core.utils.toFormat
import com.nullpointer.blogcompose.domain.auth.AuthRepository
import com.nullpointer.blogcompose.domain.notify.NotifyRepository
import com.nullpointer.blogcompose.domain.post.PostRepoImpl
import com.nullpointer.blogcompose.domain.post.PostRepository
import com.nullpointer.blogcompose.models.notify.Notify
import com.nullpointer.blogcompose.models.notify.NotifyDeserializer
import com.nullpointer.blogcompose.models.notify.TypeNotify
import com.nullpointer.blogcompose.models.notify.TypeNotify.COMMENT
import com.nullpointer.blogcompose.models.notify.TypeNotify.LIKE
import com.nullpointer.blogcompose.ui.activitys.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import timber.log.Timber
import javax.inject.Inject


@AndroidEntryPoint
class MyFirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var authRepository: AuthRepository

    @Inject
    lateinit var notifyRepository: NotifyRepository

    @Inject
    lateinit var postRepository: PostRepository

    private val job = SupervisorJob()

    private val notifyHelper = NotificationHelper()

    private val gson = GsonBuilder().registerTypeAdapter(
        Notify::class.java, NotifyDeserializer()
    ).create()

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        CoroutineScope(job).launch {
            try {
                authRepository.updateTokenUser(token)
            } catch (e: Exception) {
                when (e) {
                    is CancellationException -> throw e
                    is NullPointerException -> Timber.e("Error al actualizar el toke, el usuario es nulo")
                    else -> Timber.e("Error desconocido al actializar token $e")
                }
            }
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        CoroutineScope(job).launch {
            try {
                // * create notification
                val notify = gson.fromJson(message.data["notify"], Notify::class.java)
                // ? this is for other notifications
                when (notify.type) {
                    LIKE, COMMENT -> {
                        // * launch notification * if is validate
                        launchNotifications(notify)
                        // * lauch update databse
                        notifyRepository.requestLastNotify(true)
                        postRepository.updatePost(notify.idPost)
                    }
                }
            } catch (e: Exception) {
                Timber.e("Error al notificar $e")
            }
        }
    }

    private suspend fun launchNotifications(notify: Notify) = coroutineScope {
        val bitmapPost = async { getBitMapUser(notify.urlImgPost, false) }
        val bitmapUser = async { getBitMapUser(notify.userInNotify?.urlImg, true) }
        if (bitmapPost.await() != null && bitmapUser.await() != null) {
            notifyHelper.launchNotifyPost(
                bitmapPost = bitmapPost.await()!!,
                bitmapUser = bitmapUser.await()!!,
                nameUserLiked = notify.userInNotify?.name.toString(),
                idPost = notify.idPost,
                typeNotify = notify.type
            )
        } else {
            Timber.e("Error al obtener alguna imagen urlPost=${notify.urlImgPost} urlImgProfile=${notify.userInNotify?.urlImg}")
        }
    }

    @OptIn(ExperimentalCoilApi::class)
    private suspend fun getBitMapUser(urlImgUser: String?, circleTransform: Boolean): Bitmap? {
        val loader = ImageLoader(this)
        val request = ImageRequest.Builder(this)
            .data(urlImgUser)
            .allowHardware(false).apply {
                if (circleTransform) this.transformations(CircleCropTransformation())
            }.build()
        val result = loader.execute(request).drawable
        return result?.toBitmap()
    }

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }


    private inner class NotificationHelper {

        private val context
            get() = this@MyFirebaseMessagingService

        private fun getBaseNotification(typeNotify: TypeNotify) =
            when (typeNotify) {
                LIKE -> NotificationCompat.Builder(context,
                    "")
                    .setAutoCancel(true)
                    .setOngoing(false)
                    .setSmallIcon(R.drawable.ic_fav)
                    .setContentTitle(context.getString(R.string.text_content_notify_like))
                COMMENT -> NotificationCompat.Builder(context,
                   "")
                    .setAutoCancel(true)
                    .setOngoing(false)
                    .setSmallIcon(R.drawable.ic_comment)
                    .setContentTitle(context.getString(R.string.text_content_notify_comment))
            }


        private fun getPendingIntentCompose(idPost: String): PendingIntent {
            // * create deep link
            // * this go to post for notification
            val deepLinkIntent = Intent(Intent.ACTION_VIEW,
                "https://www.blog-compose.com/post/$idPost".toUri(),
                context,
                MainActivity::class.java)
            // * create pending intent compose
            val deepLinkPendingIntent = TaskStackBuilder.create(context).run {
                addNextIntentWithParentStack(deepLinkIntent)
                getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
            }
            return deepLinkPendingIntent
        }

        private fun createCustomNotification(
            bitmapUser: Bitmap,
            bitmapPost: Bitmap,
            nameUserLiked: String,
            typeNotify: TypeNotify,
        ): RemoteViews {
            val (title, body) = when (typeNotify) {
                LIKE -> Pair(
                    context.getString(R.string.text_title_notify_like),
                    context.getString(R.string.message_notify_liked, nameUserLiked))
                COMMENT -> Pair(
                    context.getString(R.string.text_title_notify_comment),
                    context.getString(R.string.message_notify_comment, nameUserLiked))
            }
            return RemoteViews(context.packageName, R.layout.notify_liked).apply {
                setImageViewBitmap(R.id.img_user_liked, bitmapUser)
                setImageViewBitmap(R.id.img_post_liked, bitmapPost)
                setTextViewText(R.id.text_date_notify, System.currentTimeMillis().toFormat(context))
                setTextViewText(R.id.title, title)
                setTextViewText(R.id.text, body)
            }
        }


        fun launchNotifyPost(
            bitmapPost: Bitmap,
            bitmapUser: Bitmap,
            nameUserLiked: String,
            idPost: String,
            typeNotify: TypeNotify,
        ) {
//            // * create notification channel amd get notification manager
//            val notificationManager = NotificationChannelHelper.createChannelNotification(
//                idNotificationChannel = ID_CHANNEL_POST_NOTIFY,
//                nameNotificationChanel = context.getString(NAME_CHANNEL_LIKE),
//                importance = NotificationManagerCompat.IMPORTANCE_HIGH,
//                context = context
//            )
            // * get base notify if is like or comment
            val baseNotify = getBaseNotification(typeNotify)
            // * create action to click on notification
            val deepLinkPendingIntent = getPendingIntentCompose(idPost)
            // * create custom notification
            baseNotify.setContentIntent(deepLinkPendingIntent).also {
                val customNotify = createCustomNotification(
                    bitmapUser = bitmapUser,
                    bitmapPost = bitmapPost,
                    nameUserLiked = nameUserLiked,
                    typeNotify = typeNotify)
                it.setCustomContentView(customNotify)
            }
            // * notify with random id
//            notificationManager.notify(Random.nextInt(), baseNotify.build())
        }
    }

}
