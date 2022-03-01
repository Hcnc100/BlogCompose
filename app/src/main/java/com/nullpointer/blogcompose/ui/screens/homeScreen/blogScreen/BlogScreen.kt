package com.nullpointer.blogcompose.ui.screens.homeScreen.blogScreen

import androidx.compose.animation.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.nullpointer.blogcompose.R
import com.nullpointer.blogcompose.core.states.Resource
import com.nullpointer.blogcompose.presentation.PostViewModel
import com.nullpointer.blogcompose.services.UploadPostServices
import com.nullpointer.blogcompose.ui.screens.homeScreen.blogScreen.componets.BlogItem
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect
import timber.log.Timber

@Composable
fun BlogScreen(
    postVM: PostViewModel = hiltViewModel(),
    actionGoToAddPost: () -> Unit,
) {

    val listState= rememberLazyListState()
    Scaffold(
        floatingActionButton = {
            ButtonAdd(
                isScrollInProgress = listState.isScrollInProgress,
                action = actionGoToAddPost
            )
        }
    ) {

        if(UploadPostServices.updatePostComplete.value) postVM.fetchLastPost()

        val listPostState = postVM.listPost.collectAsState()

        when (val state = listPostState.value) {
            is Resource.Failure -> {
                Timber.d(state.exception)
            }
            is Resource.Loading -> Box(modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            is Resource.Success -> {
                val listPost = state.data

                LazyColumn(state = listState) {
                    items(listPost.size) { index ->
                        BlogItem(listPost[index])
                    }
                }
                listState.OnBottomReached(3) {
                    postVM.concatenateLastPost()
                }
            }
        }
    }
}

@OptIn(InternalCoroutinesApi::class)
@Composable
fun LazyListState.OnBottomReached(
    buffer : Int = 0,
    onLoadMore : () -> Unit
) {
    require(buffer >= 0) { "buffer cannot be negative, but was $buffer" }
    val shouldLoadMore = remember {
        derivedStateOf {
            // * get the las item index and determinate if is the last minus the buffer
            val lastVisibleItem =
                layoutInfo.visibleItemsInfo.lastOrNull() ?: return@derivedStateOf true
            lastVisibleItem.index >=  layoutInfo.totalItemsCount - 1 - buffer
        }
    }
    // * listener changes and require more if is needed
    LaunchedEffect(shouldLoadMore){
        snapshotFlow { shouldLoadMore.value }
            .collect { if (it) onLoadMore() }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ButtonAdd(
    isScrollInProgress: Boolean,
    action: () -> Unit = {},
) {
    AnimatedVisibility(
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
