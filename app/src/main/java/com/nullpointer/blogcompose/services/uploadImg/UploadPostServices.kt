package com.nullpointer.blogcompose.services.uploadImg

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.nullpointer.blogcompose.core.states.StorageUploadTaskResult
import com.nullpointer.blogcompose.domain.images.ImagesRepoImpl
import com.nullpointer.blogcompose.domain.post.PostRepoImpl
import com.nullpointer.blogcompose.models.Post
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class UploadPostServices : LifecycleService() {
    companion object {
        private const val ID_CHANNEL_UPLOAD_POST = "ID_CHANNEL_UPLOAD_POST"
        private const val UPLOAD_POST_CHANNEL = "UPLOAD_POST_CHANNEL"
        private const val ACTION_START = "ACTION_START"
        private const val ACTION_STOP = "ACTION_STOP"
        private const val KEY_FILE_IMG_POST = "KEY_FILE_IMG_POST"
        private const val KEY_POST = "KEY_POST"

        fun startServicesUploadPost(context: Context, post: Post, fileImage: File) {
            Intent(context, UploadPostServices::class.java).also {
                it.putExtra(KEY_FILE_IMG_POST, fileImage)
                it.putExtra(KEY_POST, post)
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

        val updatePostComplete = mutableStateOf(false)
    }

    private val notificationHelper = NotificationHelper(this)
    private var jobUploadTask: Job? = null

    @Inject
    lateinit var imagesRepoImpl: ImagesRepoImpl

    @Inject
    lateinit var postRepoImpl: PostRepoImpl

    private val _stateUpload = MutableStateFlow<StorageUploadTaskResult?>(null)
    val stateUpload = _stateUpload.asStateFlow()

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
        val post = intent.getParcelableExtra<Post>(KEY_POST)
        if (fileImage != null && post != null) {
            lifecycleScope.launch {
                startUploadImg(Uri.fromFile(fileImage as File), post.id) {
                    postRepoImpl.updatePost(post.copy(urlImage = it))
                }
            }
        }
    }

    private fun actionStopCommand() {
        lifecycleScope.launch {
            jobUploadTask?.cancel()
        }
    }

    private suspend fun startUploadImg(
        uriImage: Uri,
        idPost: String,
        uploadPost: suspend (uri: String) -> Unit,
    ) {
        // * change state to init upload
        _stateUpload.value = StorageUploadTaskResult.Init
        val servicesNotification = notificationHelper.getNotificationUploadServices(
            ID_CHANNEL_UPLOAD_POST,
            UPLOAD_POST_CHANNEL,
            NotificationManagerCompat.IMPORTANCE_DEFAULT,
            ACTION_STOP)
        startForeground(10, servicesNotification.build())
        imagesRepoImpl.uploadImgBlog(uriImage, idPost).catch { exception ->
            // ! if has Error send error
            _stateUpload.value = StorageUploadTaskResult.Complete.Failed(Exception(exception))
            killServices()
        }.collect { task ->
            when (task) {
                is StorageUploadTaskResult.Complete.Success -> {
                    updatePostComplete.value=true
                    servicesNotification.setProgress(100, 100, true)
                    _stateUpload.value = task
                    uploadPost(task.urlFile)
                    killServices()
                }
                is StorageUploadTaskResult.InProgress -> {
                    servicesNotification.setProgress(100, task.percent, false)
                    _stateUpload.value = task
                }
                else -> Unit
            }
        }
    }

    private fun killServices() {
        updatePostComplete.value=false
        stopForeground(true)
        stopSelf()
    }

}