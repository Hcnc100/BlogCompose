package com.nullpointer.blogcompose.ui.screens.profileScreen.components.items

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.nullpointer.blogcompose.core.utils.myShimmer
import com.valentinilk.shimmer.Shimmer


@Composable
fun ItemLoadMyPost(
    shimmer: Shimmer,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(5.dp))
            .myShimmer(shimmer),
    )
}
