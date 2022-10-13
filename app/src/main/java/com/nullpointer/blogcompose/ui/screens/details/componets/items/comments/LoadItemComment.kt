package com.nullpointer.blogcompose.ui.screens.details.componets.items.comments

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import com.nullpointer.blogcompose.R
import com.nullpointer.blogcompose.core.utils.myShimmer
import com.valentinilk.shimmer.Shimmer

@Composable
fun LoadItemComment(
    shimmer: Shimmer,
    modifier: Modifier = Modifier
) {

    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier
            .padding(horizontal = 5.dp)
            .fillMaxWidth()
    ) {
        FakeImageComment(shimmer = shimmer)
        FakeComment(shimmer = shimmer)
    }

}


@Composable
private fun FakeComment(
    shimmer: Shimmer,
    modifier: Modifier = Modifier
) {
    val widthRandom = remember {
        (60..300).random()
    }
    val heightRandom = remember {
        (50..180).random()
    }
    Box(
        modifier = modifier
            .width(widthRandom.dp)
            .height(heightRandom.dp)
            .clip(RoundedCornerShape(10.dp))
            .myShimmer(shimmer),
    )
}

@Composable
private fun FakeImageComment(
    modifier: Modifier = Modifier,
    shimmer: Shimmer
) {
    Box(
        modifier = modifier
            .size(dimensionResource(id = R.dimen.size_photo_comment))
            .clip(CircleShape)
            .myShimmer(shimmer)
    )
}