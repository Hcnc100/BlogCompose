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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import kotlinx.coroutines.CoroutineScope

class PostDetailsState(
    scaffoldState: ScaffoldState,
    context: Context,
    focusManager: FocusManager,
    val lazyListState: LazyListState,
    val scope: CoroutineScope
) : SimpleScreenState(scaffoldState, context, focusManager)

@Composable
fun rememberPostDetailsState(
    context: Context = LocalContext.current,
    focusManager: FocusManager = LocalFocusManager.current,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    lazyListState: LazyListState = rememberLazyListState(),
    scope: CoroutineScope = rememberCoroutineScope()
) = remember( LazyListState) {
    PostDetailsState(scaffoldState, context, focusManager, lazyListState,scope)
}