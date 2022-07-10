package com.nullpointer.blogcompose.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nullpointer.blogcompose.R
import com.nullpointer.blogcompose.core.delegates.PropertySavableImg
import com.nullpointer.blogcompose.core.delegates.PropertySavableString
import com.nullpointer.blogcompose.models.users.MyUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
class RegistryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    companion object {
        private const val MAX_LENGTH_NAME_USER = 100
    }

    // * var to show messages
    private val _registryMessage = Channel<Int>()
    val registryMessage = _registryMessage.receiveAsFlow()



    // * fields to save state to data myUser
    val nameUser = PropertySavableString(
        state = savedStateHandle,
        label = R.string.text_label_name_user,
        hint = R.string.hint_user_name,
        maxLength = MAX_LENGTH_NAME_USER,
        emptyError = R.string.error_empty_name,
        lengthError = R.string.error_length_name
    )

    val imageProfile = PropertySavableImg(
        state = savedStateHandle,
        errorCompressImg = R.string.message_error_compress_img,
        scope = viewModelScope,
        actionSendError = _registryMessage::trySend
    )



    private val fullDataChange: Boolean
        get() = nameUser.hasChanged && imageProfile.hasChanged

    val isDataValid get() = !nameUser.hasError && !imageProfile.isEmpty


    fun getUpdatedUser(): MyUser? {
        return if(fullDataChange){
            MyUser(name = nameUser.value, urlImg = imageProfile.value.toString())
        }else{
            _registryMessage.trySend(R.string.message_error_empty_data)
            null
        }
    }


}