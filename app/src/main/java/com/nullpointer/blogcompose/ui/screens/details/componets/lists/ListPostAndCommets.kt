package com.nullpointer.blogcompose.ui.screens.details.componets.lists

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nullpointer.blogcompose.R
import com.nullpointer.blogcompose.models.Comment
import com.nullpointer.blogcompose.types.DetailsType
import com.nullpointer.blogcompose.ui.screens.details.componets.items.comments.ErrorLoadComment
import com.nullpointer.blogcompose.ui.screens.details.componets.items.comments.ItemComment
import com.nullpointer.blogcompose.ui.screens.details.componets.items.comments.LoadItemComment
import com.nullpointer.blogcompose.ui.screens.details.componets.others.TextNewComments
import com.valentinilk.shimmer.Shimmer

@Composable
fun LoadListComments(
    shimmer: Shimmer,
    sizeBetweenItems: Dp,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues,
    header: @Composable () -> Unit
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(sizeBetweenItems),
    ) {
        item(
            key = "header-posy",
            contentType = DetailsType.HEADER
        ) {
            header()
        }
        items(
            count = 10,
            key = { it },
            contentType = { DetailsType.COMMENTS },
        ) {
            LoadItemComment(shimmer = shimmer)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SuccessListComments(
    sizeBetweenItems: Dp,
    hasNewComments: Boolean,
    listComments: List<Comment>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues,
    actionReloadComments: () -> Unit,
    header: @Composable () -> Unit,
    isConcatenateComments: Boolean,
    lisState: LazyListState,
    actionConcatenateComments: () -> Unit
) {

    val showLastComment by remember {
        derivedStateOf { lisState.firstVisibleItemIndex > 0 }
    }

    Box {
        LazyColumn(
            modifier = modifier,
            contentPadding = contentPadding,
            verticalArrangement = Arrangement.spacedBy(sizeBetweenItems)
        ) {
            item(key = "header-posy", contentType = DetailsType.HEADER) {
                header()
            }
            item(
                key = "concatenate-comments",
                contentType = DetailsType.LOADER
            ) {
                when {
                    hasNewComments -> {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .padding(10.dp)
                                .size(25.dp)
                        )
                    }
                    isConcatenateComments -> {
                        Text(
                            stringResource(id = R.string.text_load_more_comments),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { actionConcatenateComments() }
                                .padding(10.dp))
                    }
                    else -> Divider()
                }
            }

            items(
                items = listComments,
                key = { it.id },
                contentType = { DetailsType.COMMENTS }) { comment ->
                ItemComment(
                    comment = comment,
                    modifier = Modifier.animateItemPlacement()
                )
            }
        }

        if (hasNewComments && showLastComment)
            TextNewComments(
                modifier = Modifier.align(Alignment.BottomCenter),
                actionReloadNewComments = actionReloadComments
            )
    }

}

@Composable
fun ErrorLoadComments(
    sizeBetweenItems: Dp,
    modifier: Modifier = Modifier,
    header: @Composable () -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(sizeBetweenItems)
    ) {
        header()
        ErrorLoadComment(actionReloadComments = { /*TODO*/ })
    }
}

