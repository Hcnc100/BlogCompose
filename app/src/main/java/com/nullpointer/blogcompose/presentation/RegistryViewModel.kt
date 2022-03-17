package com.nullpointer.blogcompose.presentation

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.imageLoader
import com.nullpointer.blogcompose.R
import com.nullpointer.blogcompose.core.delegates.SavableComposeState
import com.nullpointer.blogcompose.core.states.Resource
import com.nullpointer.blogcompose.core.states.StorageUploadTaskResult
import com.nullpointer.blogcompose.data.remote.AuthDataSource
import com.nullpointer.blogcompose.domain.auth.AuthRepoImpl
import com.nullpointer.blogcompose.domain.images.ImagesRepoImpl
import com.nullpointer.blogcompose.domain.preferences.PreferencesRepoImpl
import com.nullpointer.blogcompose.domain.toke.TokenRepoImpl
import com.nullpointer.blogcompose.models.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import me.shouheng.compress.Compress
import me.shouheng.compress.concrete
import me.shouheng.compress.strategy.config.ScaleMode
import timber.log.Timber
import java.io.File
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

@HiltViewModel
class RegistryViewModel @Inject constructor(
    private val imagesRepoImpl: ImagesRepoImpl,
    private val authRepoImpl: AuthRepoImpl,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    companion object {
        const val MAX_LENGTH_NAME_USER = 100
        private const val KEY_NAME_USER = "KEY_NAME_USER"
        private const val KEY_PHOTO_USER = "KEY_PHOTO_USER"
        private const val KEY_ERROR_NAME = "KEY_ERROR_NAME"
        private const val KEY_IMAGE_USER = "KEY_IMAGE_USER"
        private const val KEY_ERROR_IMAGE_USER = "KEY_ERROR_IMAGE_USER"
        private const val KEY_UUID_USER = "KEY_UUID_USER"
    }


    var nameUser by SavableComposeState(savedStateHandle, KEY_NAME_USER, "")
        private set
    var photoUser by SavableComposeState(savedStateHandle, KEY_PHOTO_USER, "")
        private set
    var uuidUser by SavableComposeState(savedStateHandle, KEY_UUID_USER, "")
        private set

    var errorName by SavableComposeState(savedStateHandle, KEY_ERROR_NAME, 0)
        private set
    var fileImg: File? by SavableComposeState(savedStateHandle, KEY_IMAGE_USER, null)
        private set
    var errorImage: Int by SavableComposeState(savedStateHandle, KEY_ERROR_IMAGE_USER, 0)
        private set

    private val _stateUpdateUser = MutableStateFlow<Resource<Unit>?>(null)
    val stateUpdateUser = _stateUpdateUser.asStateFlow()

    private val _registryMessage = Channel<String>()
    val registryMessage = _registryMessage.receiveAsFlow()


    private var jobCompress: Job? = null
    var isCompress = mutableStateOf(false)
        private set

    fun setInitData(name: String, urlImg: String) {
        nameUser = name
        photoUser = urlImg
    }

    fun updateDataUser(context: Context) = viewModelScope.launch {
        when {
            nameUser.isEmpty() || (photoUser.isEmpty() && fileImg == null) -> _registryMessage.send(
                "Varifique sus datos")
            fileImg == null && nameUser == nameUser -> _registryMessage.send("Sin cambios")
            else -> updateUser(context)
        }
    }

    fun changeNameUserTemp(newName: String) {
        nameUser = newName
        errorName = when {
            nameUser.isEmpty() -> R.string.error_empty_name
            nameUser.length > MAX_LENGTH_NAME_USER -> R.string.error_length_name
            else -> 0
        }
    }

    private suspend fun updateUser(context: Context) {
        _stateUpdateUser.value = Resource.Loading()
        try {
            val urlImg = updateImageUser(context, fileImg!!.toUri())
            authRepoImpl.uploadDataUser(urlImg, nameUser)
            _stateUpdateUser.value = Resource.Success(Unit)
            _registryMessage.send("Cambios guardados")
        } catch (exception: Exception) {
            when (exception) {
                is CancellationException -> throw exception
                else -> {
                    _registryMessage.send("Error desconocido")
                    _stateUpdateUser.value = Resource.Failure(exception)
                }
            }
        }
        delay(2000)
        _stateUpdateUser.value = null
    }

    suspend fun updateImageUser(context: Context, uri: Uri): String {
        return when (val result = imagesRepoImpl.uploadImgProfile(uri).last()) {
            is StorageUploadTaskResult.Complete.Failed -> throw Exception(result.error)
            is StorageUploadTaskResult.Complete.Success -> {
                // ! this very important for reload img user, with same url
                // * without this, only update img user becouse load img from cache
                context.imageLoader.memoryCache.clear()
                authRepoImpl.updatePhotoUser(result.urlFile)
            }
            else -> ""
        }
    }

    fun changeImgFileTemp(uri: Uri, context: Context) {
        jobCompress?.cancel()
        jobCompress = viewModelScope.launch {
            Timber.d("Init compress")
            isCompress.value = true
            val bitmapCompress = Compress.with(context, uri).setQuality(70).concrete {
                withMaxHeight(500f)
                withMaxWidth(500f)
                withScaleMode(ScaleMode.SCALE_SMALLER)
                withIgnoreIfSmaller(true)
            }.get(Dispatchers.IO)
            fileImg = bitmapCompress
            Timber.d("finish compress")
            isCompress.value = false
            errorImage = 0
        }
    }
}