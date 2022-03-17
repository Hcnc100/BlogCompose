package com.nullpointer.blogcompose.services.uploadImg

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.nullpointer.blogcompose.core.states.StorageUploadTaskResult
import com.nullpointer.blogcompose.domain.auth.AuthRepoImpl
import com.nullpointer.blogcompose.domain.images.ImagesRepoImpl
import com.nullpointer.blogcompose.domain.post.PostRepoImpl
import com.nullpointer.blogcompose.models.Post
import com.nullpointer.blogcompose.models.Poster
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import java.util.*
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

    private val notifyHelper = NotificationHelper(this)
    private var jobUploadTask: Job? = null

    @Inject
    lateinit var imagesRepoImpl: ImagesRepoImpl

    @Inject
    lateinit var postRepoImpl: PostRepoImpl

    @Inject
    lateinit var authRepoImpl: AuthRepoImpl


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
        val description = intent.getStringExtra(KEY_DESCRIPTION_POST)
        if (fileImage != null && description != null) {
            lifecycleScope.launch {
                val uuid = UUID.randomUUID().toString()
                startUploadImg(Uri.fromFile(fileImage as File), uuid) {
                    postRepoImpl.addNewPost(
                        post = createNewPost(uuid, description, it),
                        context = this@UploadPostServices
                    )
                }

            }
        }
    }

    suspend fun createNewPost(uuidPost: String, description: String, urlImg: String): Post {
        val user = authRepoImpl.user.first()
        return Post(
            id = uuidPost,
            description = description,
            urlImage = urlImg,
            poster = Poster(
                uuid = user.uuid,
                name = user.nameUser,
                urlImg = user.urlImg
            )
        )
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
        val servicesNotification = notifyHelper.getNotifyUploadServices(ACTION_STOP)
        startForeground(10, servicesNotification.build())
        imagesRepoImpl.uploadImgBlog(uriImage, idPost).catch { exception ->
            // ! if has Error send error
            _stateUpload.value = StorageUploadTaskResult.Complete.Failed(Exception(exception))
            killServices()
        }.collect { task ->
            when (task) {
                is StorageUploadTaskResult.Complete.Success -> {
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
        stopForeground(true)
        stopSelf()
    }

}