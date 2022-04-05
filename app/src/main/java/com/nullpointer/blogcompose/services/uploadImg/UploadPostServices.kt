package com.nullpointer.blogcompose.services.uploadImg

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.nullpointer.blogcompose.R
import com.nullpointer.blogcompose.core.states.StorageUploadTaskResult
import com.nullpointer.blogcompose.domain.auth.AuthRepoImpl
import com.nullpointer.blogcompose.domain.images.ImagesRepoImpl
import com.nullpointer.blogcompose.domain.post.PostRepoImpl
import com.nullpointer.blogcompose.models.posts.Post
import com.nullpointer.blogcompose.models.users.InnerUser
import com.nullpointer.blogcompose.services.uploadImg.NotificationChannelHelper.ID_CHANNEL_UPLOAD_POST
import com.nullpointer.blogcompose.services.uploadImg.NotificationChannelHelper.ID_NOTIFY_UPLOAD
import com.nullpointer.blogcompose.services.uploadImg.NotificationChannelHelper.NAME_UPLOAD_POST_CHANNEL
import com.nullpointer.blogcompose.ui.activitys.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class UploadPostServices : LifecycleService() {
    companion object {
        private const val ACTION_START = "ACTION_START"
        private const val ACTION_STOP = "ACTION_STOP"
        const val ACTION_GO_TO_UPLOAD = "ACTION_STOP"
        private const val KEY_FILE_IMG_POST = "KEY_FILE_IMG_POST"
        private const val KEY_DESCRIPTION_POST = "KEY_DESCRIPTION_POST"

        fun startServicesUploadPost(context: Context, description: String, fileImage: File) {
            Intent(context, UploadPostServices::class.java).also {
                it.putExtra(KEY_FILE_IMG_POST, fileImage)
                it.putExtra(KEY_DESCRIPTION_POST, description)
                it.action = ACTION_START
                context.startService(it)
            }
        }

        fun stopServicesUploadPost(context: Context) {
            Intent(context, UploadPostServices::class.java).also {
                it.action = ACTION_STOP
                context.startService(it)
            }
        }
    }

    private val notifyHelper = NotificationHelper()
    private var jobUploadTask: Job? = null

    @Inject
    lateinit var imagesRepoImpl: ImagesRepoImpl

    @Inject
    lateinit var postRepoImpl: PostRepoImpl

    @Inject
    lateinit var authRepoImpl: AuthRepoImpl




    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        intent?.let {
            when (it.action) {
                ACTION_START -> actionStartCommand(intent)
                ACTION_STOP -> actionStopCommand()
                else -> Timber.e("Se recibio un comando no valido ${it.action}")
            }
        }
        return START_STICKY
    }

    private fun actionStartCommand(intent: Intent) {
        val fileImage = intent.getSerializableExtra(KEY_FILE_IMG_POST)
        val description = intent.getStringExtra(KEY_DESCRIPTION_POST)
        if (fileImage != null && description != null) {
            lifecycleScope.launch {
                try {
                    val uuid = UUID.randomUUID().toString()
                    startUploadImg(Uri.fromFile(fileImage as File), uuid) {
                        postRepoImpl.addNewPost(
                            post = createNewPost(uuid, description, it),
                            context = this@UploadPostServices
                        )
                    }
                } catch (e: Exception) {
                    Timber.d("Error al agregar post $e")
                }
            }
        }
    }

    private suspend fun createNewPost(uuidPost: String, description: String, urlImg: String): Post {
        val user = authRepoImpl.myUser.first()
        return Post(
            id = uuidPost,
            description = description,
            urlImage = urlImg,
            userPoster = InnerUser(
                idUser = user.idUser,
                nameUser = user.nameUser,
                urlImg = user.urlImg,
            )
        )
    }

    private fun actionStopCommand() {
        Toast.makeText(this,"La publicacion del post fue cancelado",Toast.LENGTH_SHORT).show()
        jobUploadTask?.cancel()
        killServices()
    }

    private suspend fun startUploadImg(
        uriImage: Uri,
        idPost: String,
        uploadPost: suspend (uri: String) -> Unit,
    ) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val servicesNotification = notifyHelper.getNotifyUploadServices()
        startForeground(ID_NOTIFY_UPLOAD, servicesNotification.build())
        imagesRepoImpl.uploadImgBlog(uriImage, idPost).catch { exception ->
            // ! if has Error send error
            Timber.d("Error al subir la image $exception")
            killServices()
        }.collect { task ->
            when (task) {
                is StorageUploadTaskResult.Complete.Success -> {
                    servicesNotification.setProgress(0, 0, true)
                    notificationManager.notify(ID_NOTIFY_UPLOAD,servicesNotification.build())
                    uploadPost(task.urlFile)
                    killServices()
                }
                is StorageUploadTaskResult.InProgress -> {
                    Timber.d("Percent ${task.percent}")
                    servicesNotification.setProgress(100, task.percent, false)
                    notificationManager.notify(ID_NOTIFY_UPLOAD,servicesNotification.build())
                }
                else -> Unit
            }
        }
    }

    private fun killServices() {
        stopForeground(true)
        stopSelf()
    }

    private inner class NotificationHelper {

        private val context
            get() = this@UploadPostServices

        fun getNotifyUploadServices(): NotificationCompat.Builder {
            NotificationChannelHelper.createChannelNotification(
                idNotificationChannel = ID_CHANNEL_UPLOAD_POST,
                nameNotificationChanel = NAME_UPLOAD_POST_CHANNEL,
                importance = NotificationManagerCompat.IMPORTANCE_DEFAULT,
                context = context
            )
            return getNotificationUpload()
        }

        private fun getPendingIntentToClick(): PendingIntent = PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java).apply {
                action = ACTION_GO_TO_UPLOAD
            },
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        private fun getPendingIntentStop(): PendingIntent =
            PendingIntent.getService(context, 1,
                Intent(context, UploadPostServices::class.java).apply {
                    action = ACTION_STOP
                }, PendingIntent.FLAG_UPDATE_CURRENT
            )

        private fun getNotificationUpload() =
            NotificationCompat.Builder(context, ID_CHANNEL_UPLOAD_POST)
                .setOnlyAlertOnce(true)
                .setAutoCancel(false)
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_upload)
                .setContentTitle("Subiendo...")
                .setContentIntent(getPendingIntentToClick())
                .addAction(R.drawable.ic_stop, "Stop", getPendingIntentStop())
    }

}

