package com.nullpointer.blogcompose.ui.screens.details.componets.items.comments

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nullpointer.blogcompose.R
import com.nullpointer.blogcompose.ui.share.LottieContainer

@Composable
fun ErrorLoadComment(
    actionReloadComments: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
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