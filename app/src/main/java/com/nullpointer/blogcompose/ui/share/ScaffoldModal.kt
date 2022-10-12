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
    modifier: Modifier = Modifier,
    actionHideModal: () -> Unit,
    callBackSelection: (Uri) -> Unit,
    sheetState: ModalBottomSheetState,
    topBar: @Composable () -> Unit = {},
    floatingActionButtonPosition: FabPosition = FabPosition.End,
    floatingActionButton: @Composable () -> Unit = {},
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    content: @Composable (PaddingValues) -> Unit
) {
    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetContent = {
            SelectImgButtonSheet(
                isVisible = isVisibleModal,
                actionHidden = actionHideModal
            ) { uri -> uri?.let { callBackSelection(it) } }
        },
    ) {
        Scaffold(
            topBar = topBar,
            modifier = modifier,
            floatingActionButton = floatingActionButton,
            scaffoldState = scaffoldState,
            floatingActionButtonPosition = floatingActionButtonPosition,
            content = content
        )
    }
}

