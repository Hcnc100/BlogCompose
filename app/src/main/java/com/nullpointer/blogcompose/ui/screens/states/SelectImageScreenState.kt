package com.nullpointer.blogcompose.ui.screens.states

import android.content.Context
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
open class SelectImageScreenState(
    scaffoldState: ScaffoldState,
    context: Context,
    focusManager: FocusManager,
    val modalBottomSheetState: ModalBottomSheetState,
    val scope: CoroutineScope
) : SimpleScreenState(scaffoldState, context, focusManager) {

    val isShowModal get() = modalBottomSheetState.isVisible

    fun hiddenModal() {
        scope.launch {
            modalBottomSheetState.hide()
        }
    }

    fun showModal(){
        hiddenKeyBoard()
        scope.launch {
            modalBottomSheetState.show()
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun rememberSelectImageScreenState(
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    context: Context = LocalContext.current,
    focusManager: FocusManager = LocalFocusManager.current,
    scope: CoroutineScope = rememberCoroutineScope(),
    modalBottomSheetState: ModalBottomSheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
) = remember(scaffoldState, modalBottomSheetState, scope) {
    SelectImageScreenState(scaffoldState, context, focusManager, modalBottomSheetState, scope)
}