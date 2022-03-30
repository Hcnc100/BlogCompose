package com.nullpointer.blogcompose.ui.screens.swipePosts

import androidx.annotation.RawRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.nullpointer.blogcompose.models.posts.SimplePost
import com.nullpointer.blogcompose.ui.screens.emptyScreen.EmptyScreen
import com.nullpointer.blogcompose.ui.screens.homeScreen.blogScreen.componets.BlogItem
import com.nullpointer.blogcompose.ui.share.ButtonAdd
import com.nullpointer.blogcompose.ui.share.CircularProgressAnimation
import com.nullpointer.blogcompose.ui.share.OnBottomReached

@Composable
fun ScreenSwiperPost(
    resultListPost: List<SimplePost>?,
    isLoadNewData: Boolean,
    emptyString: String,
    @RawRes emptyResRaw: Int,
    actionDetails: (String, Boolean) -> Unit,
    isConcatenateData: Boolean = false,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    updateListPost: () -> Unit,
    actionBottomReached: () -> Unit,
    actionChangePost: (String, Boolean) -> Unit,
    staticInfo: Pair<String, String>? = null,
    actionButtonAdd: (() -> Unit)? = null,
    header: @Composable (() -> Unit)? = null,
) {
    // * this is necessary for hide button add when scroll
    val listState = rememberLazyListState()

    SwipeRefresh(
        state = SwipeRefreshState(isLoadNewData),
        onRefresh = { updateListPost() }
    ) {
        Scaffold(
            scaffoldState = scaffoldState,
            // * button to add new post, this hide when scroll and only
            // * show when passed action click
            floatingActionButton = {
                actionButtonAdd?.let {
                    ButtonAdd(
                        isScrollInProgress = listState.isScrollInProgress,
                        action = it
                    )
                }
            }, bottomBar = {
                CircularProgressAnimation(isVisible = isConcatenateData)
            }
        ) {
            if (resultListPost != null) {
                if (resultListPost.isEmpty()) {
                    // * show header when no has post
                    // ! this for no lost header when this list post is empty
                    Column(modifier = Modifier.padding(it)) {
                        header?.invoke()
                        EmptyScreen(resourceRaw = emptyResRaw, emptyText = emptyString)
                    }
                } else {
                    // ? show swipe list of posts
                    ListInfinitePost(
                        modifier=Modifier.padding(it),
                        listPost = resultListPost,
                        listState = listState,
                        actionBottomReached = actionBottomReached,
                        header = header,
                        actionChangePost = actionChangePost,
                        staticInfo = staticInfo,
                        actionDetails = actionDetails,
                    )
                }
            }
        }
    }
}

@Composable
fun ListInfinitePost(
    listPost: List<SimplePost>,
    listState: LazyListState,
    actionDetails: (String, Boolean) -> Unit,
    actionChangePost: (String, Boolean) -> Unit,
    modifier: Modifier= Modifier,
    header: (@Composable () -> Unit)? = null,
    actionBottomReached: () -> Unit,
    buffer: Int = 0,
    staticInfo: Pair<String, String>? = null,
) {

    LazyColumn(state = listState, modifier = Modifier) {
        // * if pass header, so add this
        header?.let {
            item { it() }
        }
        // * list post
        items(listPost.size) { index ->
            BlogItem(post = listPost[index],
                actionDetails = actionDetails,
                actionChangePost = actionChangePost,
                staticInfo = staticInfo)
        }
    }
    if (listState.layoutInfo.visibleItemsInfo.size < listState.layoutInfo.totalItemsCount)
    // * request new data when go to the last post
        listState.OnBottomReached(buffer) {
            actionBottomReached()
        }
}




