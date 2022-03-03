package com.nullpointer.blogcompose.services.notfication

import com.google.firebase.messaging.FirebaseMessagingService
import com.nullpointer.blogcompose.domain.toke.TokenRepoImpl
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
                    else->{
                        Timber.d("Error desconodico $e")
                    }
                }
            }
        }

    }

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }

}
