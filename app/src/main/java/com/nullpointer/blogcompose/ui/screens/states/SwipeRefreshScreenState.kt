package com.nullpointer.blogcompose.ui.screens.states

import android.content.Context
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState


class SwipeRefreshScreenState constructor(
    scaffoldState: ScaffoldState,
    context: Context,
    focusManager: FocusManager,
    val swipeState: SwipeRefreshState,
    val listState: LazyListState
) : SimpleScreenState(scaffoldState, context, focusManager){
    val isScrollInProgress get() = listState.isScrollInProgress
}

@Composable
fun rememberSwipeRefreshScreenState(
    isRefreshing: Boolean,
    scaffoldState: ScaffoldState= rememberScaffoldState(),
    swipeState: SwipeRefreshState= rememberSwipeRefreshState(isRefreshing = isRefreshing),
    listState: LazyListState= rememberLazyListState(),
    context: Context = LocalContext.current,
    focusManager: FocusManager = LocalFocusManager.current
)= remember(scaffoldState,swipeState,listState) {
    SwipeRefreshScreenState(scaffoldState, context, focusManager, swipeState, listState)
}