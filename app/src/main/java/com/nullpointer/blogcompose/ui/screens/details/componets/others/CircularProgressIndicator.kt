package com.nullpointer.blogcompose.ui.screens.details.componets.others

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CircularProgressComments() {
    CircularProgressIndicator(
        modifier = Modifier
            .padding(horizontal = 10.dp)
            .size(35.dp)
    )
}