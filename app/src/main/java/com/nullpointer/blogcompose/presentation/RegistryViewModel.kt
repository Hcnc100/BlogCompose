package com.nullpointer.blogcompose.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nullpointer.blogcompose.R
import com.nullpointer.blogcompose.core.delegates.PropertySavableImg
import com.nullpointer.blogcompose.core.delegates.PropertySavableString
import com.nullpointer.blogcompose.domain.compress.CompressRepository
import com.nullpointer.blogcompose.models.customSnack.MessageSnack.Companion.createErrorMessageEncode
import com.nullpointer.blogcompose.models.users.AuthUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
class RegistryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val compressRepository: CompressRepository
) : ViewModel() {
    companion object {
        private const val MAX_LENGTH_NAME_USER = 100
        private const val TAG_NAME_USER="TAG_NAME_USER"
        private const val TAG_IMG_USER="TAG_IMG_USER"
    }

    // * var to show messages
    private val _registryMessage = Channel<String>()
    val registryMessage = _registryMessage.receiveAsFlow()



    // * fields to save state to data myUser
    val nameUser = PropertySavableString(
        savedState = savedStateHandle,
        label = R.string.text_label_name_user,
        hint = R.string.hint_user_name,
        maxLength = MAX_LENGTH_NAME_USER,
        emptyError = R.string.error_empty_name,
        lengthError = R.string.error_length_name,
        tagSavable = TAG_NAME_USER
    )

    val imageProfile = PropertySavableImg(
        tagSavable = TAG_IMG_USER,
        state = savedStateHandle,
        scope = viewModelScope,
        actionCompress = compressRepository::compressImage,
        actionSendErrorCompress = {
            val message = createErrorMessageEncode(R.string.message_error_compress_img)
            _registryMessage.trySend(message)
        },
    )


    fun getUpdatedUser(): AuthUser? {
        nameUser.reValueField()
        val (user, message) = when {
            imageProfile.isEmpty && nameUser.isEmpty -> {
                val message = createErrorMessageEncode(R.string.message_error_empty_data)
                Pair(null, message)
            }
            imageProfile.isEmpty -> {
                val message = createErrorMessageEncode(R.string.need_img_user)
                Pair(null, message)
            }
            nameUser.hasError -> {
                val message = createErrorMessageEncode(R.string.need_name_user)
                Pair(null, message)
            }
            else -> {
                val user = AuthUser(
                    name = nameUser.currentValue,
                    urlImg = imageProfile.value.toString()
                )
                Pair(user, null)
            }
        }
        message?.let { _registryMessage.trySend(it) }
        return user
    }


}