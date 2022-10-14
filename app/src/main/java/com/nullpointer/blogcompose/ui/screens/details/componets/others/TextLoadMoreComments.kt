package com.nullpointer.blogcompose.ui.screens.details.componets.others

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nullpointer.blogcompose.R

@Composable
fun TextLoadMoreComments(
    actionClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .height(35.dp)
            .fillMaxWidth()
            .clickable { actionClick() },
    ) {
        Text(
            stringResource(id = R.string.text_load_more_comments),
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .align(Alignment.CenterStart)
        )
    }
}