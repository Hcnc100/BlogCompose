package com.nullpointer.blogcompose.ui.screens.states

import android.content.Context
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.LazyGridState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import kotlinx.coroutines.CoroutineScope

@ExperimentalMaterialApi
@OptIn(ExperimentalFoundationApi::class)
class ProfileScreenState constructor(
    scaffoldState: ScaffoldState,
    focusManager: FocusManager,
    context: Context,
    modalBottomSheetState: ModalBottomSheetState,
    scope: CoroutineScope,
    val swipeRefreshScreenState: SwipeRefreshScreenState,
    val listState: LazyGridState
) : SelectImageScreenState(scaffoldState, context, focusManager, modalBottomSheetState, scope) {
    @OptIn(ExperimentalFoundationApi::class)
    val isScrollInProgress get() = listState.isScrollInProgress
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
fun rememberProfileScreenState(
    isRefresh: Boolean,
    context: Context = LocalContext.current,
    focusManager: FocusManager = LocalFocusManager.current,
    gridState: LazyGridState = rememberLazyGridState(),
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    swipeRefreshScreenState: SwipeRefreshScreenState = rememberSwipeRefreshScreenState(
        isRefreshing = isRefresh
    ),
    modalBottomSheetState: ModalBottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden
    ),
) = remember(
    coroutineScope,
    scaffoldState,
    swipeRefreshScreenState,
    modalBottomSheetState,
    gridState
) {
    ProfileScreenState(
        scaffoldState,
        focusManager,
        context,
        modalBottomSheetState,
        coroutineScope,
        swipeRefreshScreenState,
        gridState
    )
}