package com.nullpointer.blogcompose.ui.share

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshState

@Composable
fun ScaffoldSwipe(
    actionOnRefresh: () -> Unit,
    swipeState: SwipeRefreshState,
    scaffoldState: ScaffoldState,
    floatingActionButton: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    SwipeRefresh(
        modifier = Modifier.fillMaxSize(),
        state = swipeState,
        onRefresh = actionOnRefresh
    ) {
        Scaffold(
            scaffoldState = scaffoldState,
            floatingActionButton = floatingActionButton,
            content = content
        )
    }
}