package com.nullpointer.blogcompose.services.notfication

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.GsonBuilder
import com.nullpointer.blogcompose.domain.auth.AuthRepository
import com.nullpointer.blogcompose.domain.notify.NotifyRepository
import com.nullpointer.blogcompose.domain.post.PostRepository
import com.nullpointer.blogcompose.models.notify.Notify
import com.nullpointer.blogcompose.models.notify.NotifyDeserializer
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


@AndroidEntryPoint
class MyFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        private val notifyServicesEvent = Channel<Unit>()
        val notifyServices = notifyServicesEvent.receiveAsFlow()
    }

    @Inject
    lateinit var authRepository: AuthRepository

    @Inject
    lateinit var notifyRepository: NotifyRepository

    @Inject
    lateinit var postRepository: PostRepository

    private val job = SupervisorJob()

    private val notifyHelper by lazy {
        NotifyFirebaseHelper(this)
    }

    private val gson by lazy {
        GsonBuilder().registerTypeAdapter(
            Notify::class.java, NotifyDeserializer()
        ).create()
    }

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
                notifyHelper.launchNotifyPost(notify)
                notifyRepository.requestLastNotifyStartWith(notify.id)
                postRepository.updatePost(notify.idPost)
                notifyServicesEvent.trySend(Unit)
            } catch (e: Exception) {
                Timber.e("Error al notificar $e")
            }
        }
    }

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }
}
