package com.nullpointer.blogcompose.ui.screens.details.componets

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ButtonHasNewComment(
    hasNewComment: Boolean,
    actionReload: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // * show button when has new comments
    if (hasNewComment) {
        Box(modifier = modifier
        ) {
            Text(text = "Hay nuevos comentarios", modifier = Modifier
                .background(MaterialTheme.colors.primary)
                .padding(horizontal = 10.dp, vertical = 10.dp)
                .clickable { actionReload() })
        }
    }
}
