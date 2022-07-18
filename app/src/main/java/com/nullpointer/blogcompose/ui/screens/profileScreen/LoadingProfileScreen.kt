package com.nullpointer.blogcompose.ui.screens.profileScreen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.GridItemSpan
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.valentinilk.shimmer.shimmer

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LoadingProfileScreen(
    headerComponent: @Composable () -> Unit
) {
    LazyVerticalGrid(
        cells = GridCells.Adaptive(100.dp),
        contentPadding = PaddingValues(4.dp)
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) { headerComponent() }
        items(20) {
            ItemImageFake()
        }
    }
}


@Composable
fun ItemImageFake() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .padding(3.dp)
            .shimmer(),
        shape = RoundedCornerShape(5.dp)
    ) {}
}
