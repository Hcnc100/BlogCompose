package com.nullpointer.blogcompose.ui.screens.states

import android.content.Context
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageView
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@ExperimentalMaterialApi
class ProfileScreenState constructor(
    context: Context,
    scope: CoroutineScope,
    focusManager: FocusManager,
    scaffoldState: ScaffoldState,
    val listState: LazyGridState,
    private val sizeScrollMore: Float,
    val swipeState: SwipeRefreshState,
    modalBottomSheetState: ModalBottomSheetState,
    launcherCropImage: ManagedActivityResultLauncher<CropImageContractOptions, CropImageView.CropResult>
) : SelectImageScreenState(
    scope = scope,
    context = context,
    focusManager = focusManager,
    scaffoldState = scaffoldState,
    launcherCropImage = launcherCropImage,
    modalBottomSheetState = modalBottomSheetState
) {


    val isScrollInProgress get() = listState.isScrollInProgress

    fun animateScrollMore() {
        scope.launch {
            listState.animateScrollBy(sizeScrollMore)
        }
    }

    fun scrollToTop() {
        scope.launch {
            listState.scrollToItem(0)
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun rememberProfileScreenState(
    sizeScrollMore: Float,
    isRefreshing: Boolean,
    actionChangeImage: (Uri) -> Unit,
    context: Context = LocalContext.current,
    focusManager: FocusManager = LocalFocusManager.current,
    gridState: LazyGridState = rememberLazyGridState(),
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    swipeState: SwipeRefreshState = rememberSwipeRefreshState(isRefreshing = isRefreshing),
    modalBottomSheetState: ModalBottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden
    ),
    launcherCropImage: ManagedActivityResultLauncher<CropImageContractOptions, CropImageView.CropResult> =
        rememberLauncherForActivityResult(CropImageContract()) { result ->
            if (result.isSuccessful) {
                result.uriContent?.let { actionChangeImage(it) }
            }
        }
) = remember(
    coroutineScope,
    scaffoldState,
    swipeState,
    modalBottomSheetState,
    gridState,
    launcherCropImage
) {
    ProfileScreenState(
        context = context,
        listState = gridState,
        scope = coroutineScope,
        swipeState = swipeState,
        focusManager = focusManager,
        scaffoldState = scaffoldState,
        sizeScrollMore = sizeScrollMore,
        launcherCropImage = launcherCropImage,
        modalBottomSheetState = modalBottomSheetState
    )
}