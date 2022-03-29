package com.nullpointer.blogcompose.services.notfication

import android.graphics.Bitmap
import androidx.core.graphics.drawable.toBitmap
import coil.ImageLoader
import coil.annotation.ExperimentalCoilApi
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.nullpointer.blogcompose.domain.auth.AuthRepoImpl
import com.nullpointer.blogcompose.domain.notify.NotifyRepoImpl
import com.nullpointer.blogcompose.domain.post.PostRepoImpl
import com.nullpointer.blogcompose.models.notify.Notify
import com.nullpointer.blogcompose.models.notify.NotifyDeserializer
import com.nullpointer.blogcompose.services.uploadImg.NotificationHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


@AndroidEntryPoint
class MyFirebaseMessagingService : FirebaseMessagingService() {
    @Inject
    lateinit var authRepoImpl: AuthRepoImpl

    @Inject
    lateinit var notifyRepoImpl: NotifyRepoImpl

    @Inject
    lateinit var postRepoImpl: PostRepoImpl

    private val job = SupervisorJob()

    private val notifyHelper = NotificationHelper(this)

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        CoroutineScope(job).launch {
            try {
                authRepoImpl.updateTokenUser(token)
            } catch (e: Exception) {
                when (e) {
                    is CancellationException -> throw e
                    is NullPointerException -> {
                        Timber.d("Error al actulizar el toke, el usurio es nulo")
                    }
                    else -> {
                        Timber.d("Error desconodico $e")
                    }
                }
            }
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        try {
            val gson = GsonBuilder()
                .registerTypeAdapter(
                    Notify::class.java,
                    NotifyDeserializer()
                ).create()
            val notify = gson.fromJson(message.data["notify"], Notify::class.java)
            CoroutineScope(job).launch {
                // * launch notification * if is validate
                launchNotifications(notify)
                // * lauch update databse
                notifyRepoImpl.requestLastNotify()
                postRepoImpl.updateLikePost(notify.idPost)
            }
        } catch (e: Exception) {
            Timber.e("Error al notificar $e")
        }
    }

    private suspend fun launchNotifications(notify: Notify) {
        val bitmapPost = getBitMapUser(notify.urlImgPost, false)
        val bitmapUser = getBitMapUser(notify.userInNotify?.urlImg, true)
        if (bitmapPost != null && bitmapUser != null) {
            notifyHelper.launchNotifyLike(
                bitmapPost = bitmapPost,
                bitmapUser = bitmapUser,
                notify.userInNotify?.nameUser.toString(),
                notify.idPost
            )
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

}
