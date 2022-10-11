package com.nullpointer.blogcompose.ui.screens.states

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageView
import com.canhub.cropper.options
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
open class SelectImageScreenState(
    context: Context,
    val scope: CoroutineScope,
    focusManager: FocusManager,
    scaffoldState: ScaffoldState,
    val modalBottomSheetState: ModalBottomSheetState,
    private val launcherCropImage: ManagedActivityResultLauncher<CropImageContractOptions, CropImageView.CropResult>
) : SimpleScreenState(scaffoldState, context, focusManager) {

    val isShowModal get() = modalBottomSheetState.isVisible

    fun hiddenModal() {
        scope.launch {
            modalBottomSheetState.hide()
        }
    }

    fun showModal() {
        hiddenKeyBoard()
        scope.launch {
            modalBottomSheetState.show()
        }
    }

    fun launchSelectImage(uri: Uri) {
        launcherCropImage.launch(options(uri = uri) {
            setFixAspectRatio(true)
            setOutputCompressQuality(100)
            setGuidelines(CropImageView.Guidelines.ON)
            setCropShape(CropImageView.CropShape.RECTANGLE)
            setActivityBackgroundColor(Color.Black.toArgb())
            setOutputCompressFormat(Bitmap.CompressFormat.JPEG)
            setMinCropResultSize(500, 500)
            setMaxCropResultSize(1000, 1000)
        })
    }

}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun rememberSelectImageScreenState(
    actionChangeImage: (Uri) -> Unit,
    context: Context = LocalContext.current,
    scope: CoroutineScope = rememberCoroutineScope(),
    focusManager: FocusManager = LocalFocusManager.current,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    modalBottomSheetState: ModalBottomSheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden),
    launcherCropImage: ManagedActivityResultLauncher<CropImageContractOptions, CropImageView.CropResult> =
        rememberLauncherForActivityResult(CropImageContract()) { result ->
            if (result.isSuccessful) {
                result.uriContent?.let { actionChangeImage(it) }
            }
        }
) = remember(scaffoldState, modalBottomSheetState, scope, launcherCropImage) {
    SelectImageScreenState(
        scope = scope,
        context = context,
        focusManager = focusManager,
        scaffoldState = scaffoldState,
        launcherCropImage = launcherCropImage,
        modalBottomSheetState = modalBottomSheetState
    )
}