package com.nullpointer.blogcompose.ui.screens.emptyScreen

import androidx.annotation.RawRes
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.nullpointer.blogcompose.ui.share.LottieContainer

@Composable
fun AnimationScreen(
    @RawRes resourceRaw: Int,
    emptyText: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        LottieContainer(
            animation = resourceRaw,
            modifier = Modifier
                .weight(3f)
                .fillMaxWidth()
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = emptyText,
                style = MaterialTheme.typography.h6,
                textAlign = TextAlign.Center
            )
        }
    }
}

