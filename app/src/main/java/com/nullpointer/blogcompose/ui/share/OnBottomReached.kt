package com.nullpointer.blogcompose.ui.share

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.*

@Composable
fun LazyListState.OnBottomReached(
    buffer: Int = 0,
    onLoadMore: () -> Unit,
) {
    require(buffer >= 0) { "buffer cannot be negative, but was $buffer" }
    val shouldLoadMore = remember {
        derivedStateOf {
            // * get the las item index and determinate if is the last minus the buffer
            val lastVisibleItem =
                layoutInfo.visibleItemsInfo.lastOrNull() ?: return@derivedStateOf true
            lastVisibleItem.index >= layoutInfo.totalItemsCount - 1 - buffer
        }
    }
    // * listener changes and require more if is needed
    LaunchedEffect(shouldLoadMore) {
        snapshotFlow { shouldLoadMore.value }
            .collect { if (it) onLoadMore() }
    }
}

@Composable
fun LazyGridState.OnBottomReached(
    buffer: Int = 0,
    onLoadMore: () -> Unit,
) {
    require(buffer >= 0) { "buffer cannot be negative, but was $buffer" }
    val shouldLoadMore = remember {
        derivedStateOf {
            // * get the las item index and determinate if is the last minus the buffer
            val lastVisibleItem =
                layoutInfo.visibleItemsInfo.lastOrNull() ?: return@derivedStateOf true
            lastVisibleItem.index >= layoutInfo.totalItemsCount - 1 - buffer
        }
    }
    // * listener changes and require more if is needed
    LaunchedEffect(shouldLoadMore) {
        snapshotFlow { shouldLoadMore.value }
            .collect { if (it) onLoadMore() }
    }
}