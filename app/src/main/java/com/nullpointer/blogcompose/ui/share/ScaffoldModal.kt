package com.nullpointer.blogcompose.ui.share

import android.net.Uri
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ScaffoldModal(
    isVisibleModal: Boolean,
    scaffoldState: ScaffoldState,
    actionHideModal: () -> Unit,
    modifier: Modifier = Modifier,
    callBackSelection: (Uri) -> Unit,
    sheetState: ModalBottomSheetState,
    topBar: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        topBar = topBar,
        modifier = modifier,
        floatingActionButton = floatingActionButton,
        scaffoldState = scaffoldState
    ) { padding ->
        ModalBottomSheetLayout(
            sheetState = sheetState,
            sheetContent = {
                SelectImgButtonSheet(
                    isVisible = isVisibleModal,
                    actionHidden = actionHideModal
                ) { uri ->
                    actionHideModal()
                    uri?.let { callBackSelection(it) }
                }
            },
        ) {
            content(padding)
        }
    }
}