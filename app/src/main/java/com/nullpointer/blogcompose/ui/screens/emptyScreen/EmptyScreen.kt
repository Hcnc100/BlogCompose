package com.nullpointer.blogcompose.ui.screens.emptyScreen

import androidx.annotation.RawRes
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.*

@Composable
fun EmptyScreen(
    @RawRes resourceRaw: Int,
    emptyText: String,
) {
    Column(modifier = Modifier.fillMaxSize()) {
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
            Text(emptyText,
                style = MaterialTheme.typography.h6,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun LottieContainer(modifier: Modifier, @RawRes animation: Int) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(animation))
    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever,
        speed = 0.5f
    )
    LottieAnimation(
        composition = composition,
        progress = progress,
        modifier = modifier,
    )
}
