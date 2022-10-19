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


open class SwipeRefreshScreenState constructor(
    context: Context,
    val scope: CoroutineScope,
    focusManager: FocusManager,
    scaffoldState: ScaffoldState,
    val listState: LazyListState,
    private val sizeScrollMore: Float,
    val swipeState: SwipeRefreshState,
) : SimpleScreenState(scaffoldState, context, focusManager) {
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
    sizeScrollMore: Float,
    isRefreshing: Boolean,
    context: Context = LocalContext.current,
    scope: CoroutineScope = rememberCoroutineScope(),
    listState: LazyListState = rememberLazyListState(),
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    focusManager: FocusManager = LocalFocusManager.current,
    swipeState: SwipeRefreshState = rememberSwipeRefreshState(isRefreshing = isRefreshing)
)= remember(scaffoldState,swipeState,listState,scope) {
    SwipeRefreshScreenState(
        scope = scope,
        context = context,
        listState = listState,
        swipeState = swipeState,
        focusManager = focusManager,
        scaffoldState = scaffoldState,
        sizeScrollMore = sizeScrollMore
    )
}