package com.nullpointer.blogcompose.services.uploadImg

import android.content.Intent
import android.net.Uri
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.nullpointer.blogcompose.R
import com.nullpointer.blogcompose.core.states.StorageUploadTaskResult
import com.nullpointer.blogcompose.core.utils.showToastMessage
import com.nullpointer.blogcompose.domain.auth.AuthRepository
import com.nullpointer.blogcompose.domain.images.ImagesRepository
import com.nullpointer.blogcompose.domain.post.PostRepository
import com.nullpointer.blogcompose.models.posts.Post
import com.nullpointer.blogcompose.models.posts.SimplePost
import com.nullpointer.blogcompose.models.users.SimpleUser
import com.nullpointer.blogcompose.services.uploadImg.UploadDataControl.ACTION_START_POST
import com.nullpointer.blogcompose.services.uploadImg.UploadDataControl.ACTION_START_USER
import com.nullpointer.blogcompose.services.uploadImg.UploadDataControl.ACTION_STOP
import com.nullpointer.blogcompose.services.uploadImg.UploadDataControl.KEY_DESC_POST_SERVICES
import com.nullpointer.blogcompose.services.uploadImg.UploadDataControl.KEY_IMG_SERVICES
import com.nullpointer.blogcompose.services.uploadImg.UploadDataControl.KEY_NAME_USER_SERVICES
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.receiveAsFlow
import timber.log.Timber
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class UploadDataServices : LifecycleService() {

    private enum class TypeUpdate {
        POST, USER
    }

    companion object {
        private val finishUploadSuccessEvent = Channel<Unit>()
        val finishUploadSuccess = finishUploadSuccessEvent.receiveAsFlow()
    }

    private val notifyHelper by lazy { NotifyUploadImgHelper(this) }
    private var jobUploadTask: Job? = null

    @Inject
    lateinit var imagesRepository: ImagesRepository

    @Inject
    lateinit var postRepository: PostRepository

    @Inject
    lateinit var authRepository: AuthRepository

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        intent?.let {
            when (it.action) {
                ACTION_START_POST -> startUploadData(intent, TypeUpdate.POST)
                ACTION_START_USER -> startUploadData(intent, TypeUpdate.USER)
                ACTION_STOP -> actionStopCommand()
                else -> Timber.e("Command services unknown ${it.action}")
            }
        }
        return START_STICKY
    }

    private fun startUploadData(intent: Intent, typeUpdate: TypeUpdate) {
        jobUploadTask = lifecycleScope.launch {
            try {
                val uriImg = intent.getParcelableExtra<Uri>(KEY_IMG_SERVICES)!!
                when (typeUpdate) {
                    TypeUpdate.POST -> {
                        val description = intent.getStringExtra(KEY_DESC_POST_SERVICES)!!
                        val uuid = UUID.randomUUID().toString()
                        startUploadImg(
                            uriImage = uriImg,
                            idImgUpload = uuid,
                            typeUpdate = typeUpdate
                        ) {
                            withContext(Dispatchers.IO){
                                postRepository.addNewPost(
                                    post = createNewPost(
                                        uuid,
                                        description,
                                        it
                                    ) as Post
                                )
                            }
                        }
                        finishUploadSuccessEvent.trySend(Unit)
                        showToastMessage(R.string.post_upload_success)
                    }
                    TypeUpdate.USER -> {
                        val name = intent.getStringExtra(KEY_NAME_USER_SERVICES)
                        startUploadImg(
                            uriImage = uriImg,
                            typeUpdate = typeUpdate
                        ) {
                            withContext(Dispatchers.IO) {
                                authRepository.uploadDataUser(urlImg = it, name = name)
                            }
                        }
                        showToastMessage(R.string.data_user_upload_sucess)
                    }
                }

            } catch (e: Exception) {
                when (e) {
                    is CancellationException -> throw e
                    else ->{
                        Timber.e("Error server upload service $e")
                        showToastMessage(R.string.error_upload_service)
                    }
                }
                killServices()
            }
        }
    }


    private suspend fun createNewPost(
        uuidPost: String,
        description: String,
        urlImg: String
    ): SimplePost {
        val user = authRepository.myUser.first()
        return Post(
            id = uuidPost,
            description = description,
            urlImage = urlImg,
            userPoster = SimpleUser(
                idUser = user.idUser,
                name = user.name,
                urlImg = user.urlImg,
            )
        )
    }

    private suspend fun startUploadImg(
        uriImage: Uri,
        typeUpdate: TypeUpdate,
        idImgUpload: String = "",
        uploadPost: suspend (uri: String) -> Unit,
    ) {
        notifyHelper.startServicesForeground(this)

        val taskUpload = when (typeUpdate) {
            TypeUpdate.POST -> imagesRepository.uploadImgBlog(uriImage, idImgUpload)
            TypeUpdate.USER -> imagesRepository.uploadImgProfile(uriImage)
        }

        taskUpload.catch { exception ->
            // ! if has Error send error
            Timber.d("Error upload img $exception")
            killServices()
        }.flowOn(Dispatchers.IO)
            .collect { task ->
                when (task) {
                    is StorageUploadTaskResult.Complete.Failed -> {
                        Timber.d("Error upload img ${task.error}")
                        killServices()
                    }
                    is StorageUploadTaskResult.Complete.Success -> {
                        notifyHelper.updateNotifyFinishUpdate()
                        uploadPost(task.urlFile)
                        killServices()
                    }
                    is StorageUploadTaskResult.InProgress -> {
                        Timber.d("Percent ${task.percent}")
                        notifyHelper.updateNotifyProgressUpload(task.percent)
                    }
                    else -> Unit
                }
            }
    }

    private fun killServices() {
        stopForeground(true)
        stopSelf()
    }

    private fun actionStopCommand() {
        showToastMessage(R.string.message_cancel_upload_services)
        jobUploadTask?.cancel()
        killServices()
    }


}

