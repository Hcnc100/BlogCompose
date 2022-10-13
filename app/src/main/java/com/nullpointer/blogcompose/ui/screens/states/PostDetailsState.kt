package com.nullpointer.blogcompose.ui.screens.states

import android.content.Context
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class PostDetailsState(
    context: Context,
    val scope: CoroutineScope,
    focusManager: FocusManager,
    scaffoldState: ScaffoldState,
    val lazyListState: LazyListState,
    val focusRequester: FocusRequester
) : SimpleScreenState(scaffoldState, context, focusManager) {
    fun requestFocus() {
        focusRequester.requestFocus()
    }

    fun scrollToNewComment() = scope.launch {
//        lazyListState.animateScrollToItem(lazyListState.layoutInfo.totalItemsCount - 1)
    }
}

@Composable
fun rememberPostDetailsState(
    context: Context = LocalContext.current,
    scope: CoroutineScope = rememberCoroutineScope(),
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    lazyListState: LazyListState = rememberLazyListState(),
    focusManager: FocusManager = LocalFocusManager.current,
    focusRequester: FocusRequester = remember { FocusRequester() }
) = remember(LazyListState, scaffoldState, scope, focusRequester) {
    PostDetailsState(
        scope = scope,
        context = context,
        focusManager = focusManager,
        scaffoldState = scaffoldState,
        lazyListState = lazyListState,
        focusRequester = focusRequester
    )
}