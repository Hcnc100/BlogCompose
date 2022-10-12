package com.nullpointer.blogcompose.ui.share

import android.net.Uri
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshState


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ScaffoldModalSwipe(
    isVisibleModal: Boolean,
    modifier: Modifier = Modifier,
    actionOnRefresh: () -> Unit,
    actionHideModal: () -> Unit,
    swipeState: SwipeRefreshState,
    callBackSelection: (Uri) -> Unit,
    sheetState: ModalBottomSheetState,
    topBar: @Composable () -> Unit = {},
    floatingActionButtonPosition: FabPosition = FabPosition.End,
    floatingActionButton: @Composable () -> Unit = {},
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    content: @Composable (PaddingValues) -> Unit
) {
    SwipeRefresh(
        modifier = Modifier.fillMaxSize(),
        state = swipeState,
        onRefresh = actionOnRefresh
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
                content = content,
                modifier = modifier,
                scaffoldState = scaffoldState,
                floatingActionButton = floatingActionButton,
                floatingActionButtonPosition = floatingActionButtonPosition
            )
        }
    }
}

