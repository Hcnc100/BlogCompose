package com.nullpointer.blogcompose.services.notfication

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import coil.transform.CircleCropTransformation
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.nullpointer.blogcompose.domain.notify.NotifyRepoImpl
import com.nullpointer.blogcompose.domain.preferences.PreferencesRepoImpl
import com.nullpointer.blogcompose.domain.toke.TokenRepoImpl
import com.nullpointer.blogcompose.models.Notify
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
    lateinit var tokenRepoImpl: TokenRepoImpl

    @Inject
    lateinit var notifyRepoImpl: NotifyRepoImpl

    private val job = SupervisorJob()

    private val notifyHelper = NotificationHelper(this)

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        CoroutineScope(job).launch {
            try {
                tokenRepoImpl.updateCurrentToken(token)
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
            val notify = Notify(message.data)
            Timber.d("notificacion recibida $notify")
            CoroutineScope(job).launch {
                notifyHelper.launchNotifyLike(
                    bitmapPost = getBitMapUser(notify.urlImgPost, false)!!,
                    bitmapUser = getBitMapUser(notify.imgUserLiked, true)!!,
                    notify.nameUserLiked,
                    ""
                )
                notifyRepoImpl.requestLastNotify()
            }
        } catch (e: Exception) {
            Timber.e("Message $e")
        }
    }

    private suspend fun getBitMapUser(urlImgUser: String, circleTransform: Boolean): Bitmap? {
        val loader = ImageLoader(this)
        val request = ImageRequest.Builder(this)
            .data(urlImgUser)
            .allowHardware(false).apply {
                if (circleTransform) this.transformations(CircleCropTransformation())
            }.build()
        val result = (loader.execute(request) as SuccessResult).drawable
        return (result as BitmapDrawable).bitmap
    }

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }

}
