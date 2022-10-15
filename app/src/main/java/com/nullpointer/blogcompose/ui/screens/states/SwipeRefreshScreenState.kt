package com.nullpointer.blogcompose.ui.screens.states

import android.content.Context
import androidx.compose.foundation.gestures.animateScrollBy
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
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


class SwipeRefreshScreenState constructor(
    private val sizeScrollMore:Float,
    scaffoldState: ScaffoldState,
    context: Context,
    focusManager: FocusManager,
    val swipeState: SwipeRefreshState,
    val listState: LazyListState,
    val scope: CoroutineScope,
) : SimpleScreenState(scaffoldState, context, focusManager){
    val isScrollInProgress get() = listState.isScrollInProgress

    fun animateScrollMore() {
        scope.launch {
            listState.animateScrollBy(sizeScrollMore)
        }
    }

    fun scrollToTop() {
        scope.launch {
            listState.scrollToItem(0)
        }
    }
}

@Composable
fun rememberSwipeRefreshScreenState(
    sizeScrollMore:Float,
    isRefreshing: Boolean,
    scaffoldState: ScaffoldState= rememberScaffoldState(),
    swipeState: SwipeRefreshState= rememberSwipeRefreshState(isRefreshing = isRefreshing),
    listState: LazyListState= rememberLazyListState(),
    context: Context = LocalContext.current,
    focusManager: FocusManager = LocalFocusManager.current,
    scope: CoroutineScope = rememberCoroutineScope()
)= remember(scaffoldState,swipeState,listState,scope) {
    SwipeRefreshScreenState(sizeScrollMore,scaffoldState, context, focusManager, swipeState, listState,scope)
}