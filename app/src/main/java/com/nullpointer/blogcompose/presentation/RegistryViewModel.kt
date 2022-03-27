package com.nullpointer.blogcompose.presentation

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.imageLoader
import com.nullpointer.blogcompose.R
import com.nullpointer.blogcompose.core.delegates.SavableComposeState
import com.nullpointer.blogcompose.core.states.Resource
import com.nullpointer.blogcompose.core.states.StorageUploadTaskResult
import com.nullpointer.blogcompose.core.utils.NetworkException
import com.nullpointer.blogcompose.domain.auth.AuthRepoImpl
import com.nullpointer.blogcompose.domain.images.ImagesRepoImpl
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
    }

    // * fields to save state to data user
    var nameUser by SavableComposeState(savedStateHandle, KEY_NAME_USER, "")
        private set
    var photoUser by SavableComposeState(savedStateHandle, KEY_PHOTO_USER, "")
        private set
    var errorName by SavableComposeState(savedStateHandle, KEY_ERROR_NAME, 0)
        private set
    var fileImg: File? by SavableComposeState(savedStateHandle, KEY_IMAGE_USER, null)
        private set

    // * var to save state data change
    private val _stateUpdateUser = MutableStateFlow<Resource<Unit>?>(null)
    val stateUpdateUser = _stateUpdateUser.asStateFlow()

    // * var to show messages
    private val _registryMessage = Channel<String>()
    val registryMessage = _registryMessage.receiveAsFlow()

    // * var to save job compress img
    private var jobCompress: Job? = null

    // * var to save state compress image
    private val _stateCompressImage = MutableStateFlow<Resource<Unit>>(Resource.Success(Unit))
    val stateCompressImg = _stateCompressImage.asStateFlow()

    private var oldName: String = ""

    init {
        Timber.d("Se inicio el registry view model")
        viewModelScope.launch {
            try {
                // * get info for user saved
                val currentUser = authRepoImpl.user.first()
                nameUser = currentUser.nameUser
                photoUser = currentUser.urlImg

                // * change var to know when name is change or no
                oldName = nameUser
            } catch (e: Exception) {
                if (e is CancellationException) throw e else Timber.e("$e")
            }
        }
    }

    fun changeNameUserTemp(newName: String) {
        // * when change name in input, saved itno view model
        nameUser = newName
        // * show error if is needed
        errorName = when {
            nameUser.isEmpty() -> R.string.error_empty_name
            nameUser.length > MAX_LENGTH_NAME_USER -> R.string.error_length_name
            else -> 0
        }
    }

    fun updateDataUser(context: Context) = viewModelScope.launch {
        when {
            // * when no has data user send error message
            nameUser.isEmpty() || (photoUser.isEmpty() && fileImg == null) -> _registryMessage.send(
                "Varifique sus datos")
            // * when data no is change send error message
            fileImg == null && oldName == nameUser -> _registryMessage.send("Sin cambios")
            // * else update
            else -> updateUser(context)
        }
    }

    private suspend fun updateUser(context: Context) {
        // * change state update
        _stateUpdateUser.value = Resource.Loading()
        try {
            // * upload img to user profile if no is null and get url
            val urlImg = fileImg?.toUri()?.let { updateImageUser(context, it) }
            // * update image user if no is null and name if is different from name saved
            authRepoImpl.uploadDataUser(urlImg, if (oldName != nameUser) nameUser else null)
            // * if is success update state
            _stateUpdateUser.value = Resource.Success(Unit)
            // * notify to user
            _registryMessage.send("Cambios guardados")
        } catch (exception: Exception) {
            // * show error message if has exception
            when (exception) {
                is CancellationException -> throw exception
                is NetworkException -> _registryMessage.send("Se necesita internet para actulizar sus datos")
                else -> {
                    Timber.e("Error al actulizar datos $exception")
                    _registryMessage.send("Error desconocido")
                    _stateUpdateUser.value = Resource.Failure(exception)
                }
            }
        }
        // * delay for show animation in ui
        // ! this is no necessary
        delay(2000)
        _stateUpdateUser.value = null
    }

    private suspend fun updateImageUser(context: Context, uri: Uri): String? {
        // * get the last state to update image user
        // * this only can Failed or Success state
        // ! if is other state return null for no update image user
        return when (val result = imagesRepoImpl.uploadImgProfile(uri).last()) {
            is StorageUploadTaskResult.Complete.Failed -> throw Exception(result.error)
            is StorageUploadTaskResult.Complete.Success -> {
                // ! this very important for reload img user, with same url
                // * without this, only update img user because load img from cache
                context.imageLoader.memoryCache.clear()
                result.urlFile
            }
            else -> null
        }
    }

    fun changeImgFileTemp(uri: Uri, context: Context) {
        // * if has another job to compress cancel this
        jobCompress?.cancel()
        // * init another job for compress
        jobCompress = viewModelScope.launch {
            // * change state compress
            _stateCompressImage.value = Resource.Loading()
            _stateCompressImage.value = try {
                // * init process compress img
                val bitmapCompress = Compress.with(context, uri).setQuality(70).concrete {
                    withMaxHeight(500f)
                    withMaxWidth(500f)
                    withScaleMode(ScaleMode.SCALE_SMALLER)
                    withIgnoreIfSmaller(true)
                }.get(Dispatchers.IO)
                // * save field with file img compress
                fileImg = bitmapCompress
                Resource.Success(Unit)
            } catch (e: Exception) {
                when (e) {
                    is CancellationException -> throw e
                    else -> {
                        Timber.e("Error al comprimir imagen $e")
                        _registryMessage.send("No se pudo obtener la imagen, reintente")
                    }
                }
                Resource.Failure(e)
            }
        }
    }
}