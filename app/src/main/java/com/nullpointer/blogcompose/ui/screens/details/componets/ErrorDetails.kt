package com.nullpointer.blogcompose.ui.screens.details.componets

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.nullpointer.blogcompose.R
import com.nullpointer.blogcompose.models.posts.Post
import com.nullpointer.blogcompose.ui.screens.emptyScreen.AnimationScreen

@Composable
fun ErrorLoadingOnlyComments(
    post: Post,
    modifier: Modifier = Modifier,
    actionLike: () -> Unit
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
    ) {
        HeaderBlogDetails(blog = post, actionLike =actionLike)
        AnimationScreen(
            resourceRaw = R.raw.error1, emptyText = stringResource(
                id = R.string.error_load_comments
            )
        )
    }
}