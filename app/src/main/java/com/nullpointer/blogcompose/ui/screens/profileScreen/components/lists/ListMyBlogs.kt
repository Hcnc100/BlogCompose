package com.nullpointer.blogcompose.ui.screens.profileScreen.components.lists

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nullpointer.blogcompose.R
import com.nullpointer.blogcompose.models.posts.MyPost
import com.nullpointer.blogcompose.types.ProfileTypes
import com.nullpointer.blogcompose.ui.screens.emptyScreen.AnimationScreen
import com.nullpointer.blogcompose.ui.screens.profileScreen.components.items.ItemLoadMyPost
import com.nullpointer.blogcompose.ui.screens.profileScreen.components.items.ItemMyPost
import com.nullpointer.blogcompose.ui.share.CircularProgressAnimation
import com.nullpointer.blogcompose.ui.share.OnBottomReached
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ListSuccessMyBlogs(
    spaceBetweenItems: Dp,
    listMyPost: List<MyPost>,
    gridState: LazyGridState,
    actionLoadMore: () -> Unit,
    isConcatenateMyBlog: Boolean,
    contentPadding: PaddingValues,
    actionClickPost: (String) -> Unit,
    header: @Composable () -> Unit
) {

    Box {
        LazyVerticalGrid(
            contentPadding = contentPadding,
            verticalArrangement = Arrangement.spacedBy(spaceBetweenItems),
            horizontalArrangement = Arrangement.spacedBy(spaceBetweenItems),
            columns = GridCells.Adaptive(dimensionResource(id = R.dimen.size_photo_my_post)),
            state = gridState
        ) {
            item(key = "header-profile", span = { GridItemSpan(maxLineSpan) }) {
                header()
            }

            items(listMyPost, key = { it.id }) { post ->
                ItemMyPost(
                    post = post,
                    actionDetails = actionClickPost,
                    modifier = Modifier.animateItemPlacement()
                )
            }
        }
        CircularProgressAnimation(
            isVisible = isConcatenateMyBlog,
            modifier = Modifier.align(
                Alignment.BottomCenter
            )
        )
    }

    gridState.OnBottomReached(buffer = 0, onLoadMore = actionLoadMore)
}

@Composable
fun ListEmptyMyBlogs(
    modifier: Modifier = Modifier,
    header: @Composable () -> Unit
) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {

        header()

        AnimationScreen(
            resourceRaw = R.raw.empty5,
            emptyText = stringResource(id = R.string.message_empty_my_post),
            modifier = Modifier
                .fillMaxWidth()
                .height(350.dp)
        )

    }

}

@Composable
fun ListLoadMyBlogs(
    spaceBetweenItems: Dp,
    contentPadding: PaddingValues,
    header: @Composable () -> Unit
) {
    val shimmer = rememberShimmer(shimmerBounds = ShimmerBounds.View)
    LazyVerticalGrid(
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(spaceBetweenItems),
        horizontalArrangement = Arrangement.spacedBy(spaceBetweenItems),
        columns = GridCells.Adaptive(dimensionResource(id = R.dimen.size_photo_my_post))
    ) {
        item(
            key = "header-profile",
            span = { GridItemSpan(maxLineSpan) },
            contentType = ProfileTypes.INFO_PROFILE
        ) {
            header()
        }
        items(
            count = 20,
            key = { it },
            contentType = { ProfileTypes.GRID_POST }
        ) {
            ItemLoadMyPost(shimmer = shimmer)
        }
    }
}