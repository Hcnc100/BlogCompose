package com.nullpointer.blogcompose.ui.screens.details.componets.items.post

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.nullpointer.blogcompose.R
import com.nullpointer.blogcompose.ui.screens.emptyScreen.AnimationScreen

@Composable
fun ErrorDetailsPost(
    modifier: Modifier = Modifier,
) {
    AnimationScreen(
        modifier = modifier,
        resourceRaw = R.raw.error1,
        emptyText = stringResource(id = R.string.error_load_post)
    )
}