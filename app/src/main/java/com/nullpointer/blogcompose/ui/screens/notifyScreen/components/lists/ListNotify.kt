package com.nullpointer.blogcompose.ui.screens.notifyScreen.components.lists

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
import androidx.compose.ui.unit.Dp
import com.nullpointer.blogcompose.R
import com.nullpointer.blogcompose.models.notify.Notify
import com.nullpointer.blogcompose.ui.screens.emptyScreen.AnimationScreen
import com.nullpointer.blogcompose.ui.screens.notifyScreen.components.items.ItemLoadNotify
import com.nullpointer.blogcompose.ui.screens.notifyScreen.components.items.ItemNotify
import com.nullpointer.blogcompose.ui.share.CircularProgressAnimation
import com.nullpointer.blogcompose.ui.share.OnBottomReached
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer

@Composable
fun LoadListNotify(
    sizeBetweenItems: Dp,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues
) {
    val shimmer = rememberShimmer(shimmerBounds = ShimmerBounds.View)
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(sizeBetweenItems)
    ) {
        items(10, key = { it }) {
            ItemLoadNotify(shimmer = shimmer)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SuccessListNotify(
    sizeBetweenItems: Dp,
    listNotify: List<Notify>,
    actionRequestMore: () -> Unit,
    isConcatenateNotify: Boolean,
    lazyListState: LazyListState,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
    actionClickNotify: (Notify) -> Unit
) {
    Box {
        LazyColumn(
            modifier = modifier,
            state = lazyListState,
            contentPadding = contentPadding,
            verticalArrangement = Arrangement.spacedBy(sizeBetweenItems)
        ) {
            // * all notifications
            items(
                listNotify,
                key = { it.id }
            ) { notify ->
                ItemNotify(
                    notify = notify,
                    actionClick = actionClickNotify,
                    modifier = Modifier.animateItemPlacement()
                )
            }
        }

        CircularProgressAnimation(
            isVisible = isConcatenateNotify,
            modifier = Modifier.align(Alignment.BottomCenter)
        )

    }

    lazyListState.OnBottomReached(0, actionRequestMore)
}

@Composable
fun EmptyListNotify(
    modifier: Modifier = Modifier
) {
    AnimationScreen(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        resourceRaw = R.raw.empty3,
        emptyText = stringResource(R.string.message_empty_notify)
    )
}