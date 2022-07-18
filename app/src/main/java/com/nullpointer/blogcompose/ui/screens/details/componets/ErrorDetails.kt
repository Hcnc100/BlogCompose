package com.nullpointer.blogcompose.ui.screens.details.componets

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nullpointer.blogcompose.R
import com.nullpointer.blogcompose.models.posts.Post
import com.nullpointer.blogcompose.ui.share.LottieContainer

@Composable
fun ErrorLoadingOnlyComments(
    post: Post,
    modifier: Modifier = Modifier,
    actionReloadComments:()->Unit,
    actionLike: () -> Unit
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
    ) {
        HeaderBlogDetails(blog = post, actionLike = actionLike)
        LottieContainer(
            animation = R.raw.error3,
            modifier = Modifier
                .size(250.dp)
                .align(Alignment.CenterHorizontally)
        )
        OutlinedButton(
            onClick = actionReloadComments,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = stringResource(id = R.string.retry_load_comments))
        }
    }
}