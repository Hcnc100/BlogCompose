package com.nullpointer.blogcompose.ui.screens.details.componets.others

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nullpointer.blogcompose.R

@Composable
fun TextNewComments(
    modifier: Modifier = Modifier,
    actionReloadNewComments: () -> Unit
) {
    Text(
        stringResource(id = R.string.message_has_new_comments),
        modifier = modifier
            .padding(10.dp)
            .background(MaterialTheme.colors.primary)
            .padding(10.dp)
            .clip(RoundedCornerShape(10.dp))
            .clickable { actionReloadNewComments() }

    )
}