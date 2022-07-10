package com.nullpointer.blogcompose.ui.screens.states

import android.content.Context
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import com.nullpointer.blogcompose.core.utils.SimpleScreenState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
@OptIn(ExperimentalMaterialApi::class)
class DataUserScreenState  constructor(
    scaffoldState: ScaffoldState,
    context: Context,
    focusManager: FocusManager,
    val modalBottomSheetState: ModalBottomSheetState,
    private val scope: CoroutineScope
) : SimpleScreenState(scaffoldState, context, focusManager) {

    val isShowModal=modalBottomSheetState.isVisible

    fun hiddenModal() {
        scope.launch {
            modalBottomSheetState.hide()
        }
    }

    fun showModal(){
        scope.launch {
            modalBottomSheetState.show()
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun rememberDataUserScreenState(
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    context: Context = LocalContext.current,
    focusManager: FocusManager = LocalFocusManager.current,
    scope: CoroutineScope = rememberCoroutineScope(),
    modalBottomSheetState: ModalBottomSheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
) = remember(scaffoldState, modalBottomSheetState, scope) {
    DataUserScreenState(scaffoldState, context, focusManager, modalBottomSheetState, scope)
}