package com.nullpointer.blogcompose.ui.screens.blogScreen.components.list

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nullpointer.blogcompose.R
import com.nullpointer.blogcompose.models.posts.Post
import com.nullpointer.blogcompose.models.posts.SimplePost
import com.nullpointer.blogcompose.ui.screens.blogScreen.ActionsPost
import com.nullpointer.blogcompose.ui.screens.blogScreen.components.items.BlogItem
import com.nullpointer.blogcompose.ui.screens.blogScreen.components.items.BlogLoadItem
import com.nullpointer.blogcompose.ui.screens.emptyScreen.AnimationScreen
import com.nullpointer.blogcompose.ui.share.CircularProgressAnimation
import com.nullpointer.blogcompose.ui.share.OnBottomReached
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer

@Composable
fun ListLoadBlog(
    modifier: Modifier = Modifier
) {
    val shimmer = rememberShimmer(shimmerBounds = ShimmerBounds.View)
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(5.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        items(5, key = { it }) {
            BlogLoadItem(shimmer = shimmer)
        }
    }
}

@Composable
fun ListEmptyBlog(modifier: Modifier) {
    AnimationScreen(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        resourceRaw = R.raw.empty1,
        emptyText = stringResource(id = R.string.message_empty_post)
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ListSuccessBlog(
    listPost: List<Post>,
    isConcatenate: Boolean,
    listState: LazyListState,
    modifier: Modifier = Modifier,
    actionBottomReached: () -> Unit,
    actionBlog: (ActionsPost, SimplePost) -> Unit
) {

    Box {
        LazyColumn(
            state = listState,
            modifier = modifier,
            contentPadding = PaddingValues(5.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(
                listPost,
                key = { it.id }
            ) { post ->
                BlogItem(
                    post = post,
                    actionBlog = actionBlog,
                    modifier = Modifier.animateItemPlacement()
                )
            }
        }

        CircularProgressAnimation(
            isVisible = isConcatenate,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }


    // * when go to the finish list, request more post
    listState.OnBottomReached(0, actionBottomReached)
}
