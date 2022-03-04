package com.nullpointer.blogcompose.services.notfication

import android.view.inputmethod.CorrectionInfo
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.nullpointer.blogcompose.domain.preferences.PreferencesRepoImpl
import com.nullpointer.blogcompose.domain.toke.TokenRepoImpl
import com.nullpointer.blogcompose.models.NotifyBasic
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MyFirebaseMessagingService : FirebaseMessagingService() {
    companion object {
        private const val ID_USER = "ID_USER"
        private const val USER_URL_IMG = "USER_URL_IMG"
        private const val NAME_USER_LIKE = "NAME_USER_LIKE"
        private const val POST_URL_IMG = "POST_URL_IMG"
    }

    @Inject
    lateinit var tokenRepoImpl: TokenRepoImpl

    @Inject
    lateinit var preferencesRepoImpl: PreferencesRepoImpl

    private val job = SupervisorJob()
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
            val notify=NotifyBasic(message.data)
            Timber.d("notificacion recibida $notify")
        }catch (e:Exception){
            Timber.e("Message $e")
        }
    }

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }

}
