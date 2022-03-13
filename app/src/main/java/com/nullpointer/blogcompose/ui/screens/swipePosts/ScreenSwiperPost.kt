package com.nullpointer.blogcompose.ui.screens.swipePosts

import androidx.compose.animation.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.nullpointer.blogcompose.R
import com.nullpointer.blogcompose.core.states.Resource
import com.nullpointer.blogcompose.models.Post
import com.nullpointer.blogcompose.models.SimplePost
import com.nullpointer.blogcompose.ui.screens.homeScreen.blogScreen.componets.BlogItem
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect

@Composable
fun ScreenSwiperPost(
    resultListPost: List<SimplePost>,
    isLoadNewData: Boolean,
    isConcatenateData: Boolean = false,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    updateListPost: () -> Unit,
    actionBottomReached: () -> Unit,
    actionChangePost: (String, Boolean) -> Unit,
    staticInfo: Pair<String, String>? = null,
    actionButtonAdd: (() -> Unit)? = null,
    header: @Composable (() -> Unit)? = null,
) {
    val listState = rememberLazyListState()

    SwipeRefresh(
        state = SwipeRefreshState(isLoadNewData),
        onRefresh = { updateListPost() }
    ) {
        Scaffold(
            scaffoldState = scaffoldState,
            floatingActionButton = {
                actionButtonAdd?.let {
                    ButtonAdd(
                        isScrollInProgress = listState.isScrollInProgress,
                        action = it
                    )
                }
            }
        ) {
            ListInfinitePost(
                listPost = resultListPost,
                listState = listState,
                actionBottomReached = actionBottomReached,
                header = header,
                actionChangePost = actionChangePost,
                isConcatenateData = isConcatenateData,
                staticInfo = staticInfo
            )

        }
    }
}

@Composable
fun ListInfinitePost(
    listPost: List<SimplePost>,
    listState: LazyListState,
    actionChangePost: (String, Boolean) -> Unit,
    header: (@Composable () -> Unit)? = null,
    actionBottomReached: () -> Unit,
    isConcatenateData: Boolean,
    buffer: Int = 0,
    staticInfo: Pair<String, String>? = null,
) {
    LazyColumn(state = listState) {
        header?.let {
            item { it() }
        }
        items(listPost.size) { index ->
            BlogItem(listPost[index], actionChangePost, staticInfo)
        }

        item {
            AnimatedVisibility(
                visible = isConcatenateData && listPost.isNotEmpty(),
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp),
                    contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

        }


    }
    // ! this is import for no call with this list is empty
    if(listPost.isNotEmpty()){
        listState.OnBottomReached(buffer) {
            actionBottomReached()
        }
    }

}

@OptIn(InternalCoroutinesApi::class)
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

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ButtonAdd(
    modifier: Modifier = Modifier,
    isScrollInProgress: Boolean,
    action: () -> Unit,
) {
    AnimatedVisibility(
        modifier = modifier,
        visible = !isScrollInProgress,
        enter = scaleIn() + fadeIn(),
        exit = scaleOut() + fadeOut()
    ) {
        FloatingActionButton(onClick = { action() }) {
            Icon(painterResource(id = R.drawable.ic_add),
                contentDescription = "")
        }
    }
}
