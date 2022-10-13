package com.nullpointer.blogcompose.ui.screens.details.componets.items.post

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import com.nullpointer.blogcompose.R
import com.nullpointer.blogcompose.core.utils.myShimmer
import com.valentinilk.shimmer.Shimmer

@Composable
fun LoadDetailsPost(
    modifier: Modifier = Modifier,
    shimmer: Shimmer,
) {

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        FakeDetailUser(shimmer = shimmer)
        Column(
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            repeat(3) {
                FakeInfoBlog(shimmer = shimmer)
            }
        }
        FakeImageBlog(
            shimmer = shimmer,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
fun FakeImageBlog(
    shimmer: Shimmer,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .height(300.dp)
            .width(250.dp)
            .clip(RoundedCornerShape(5.dp))
            .myShimmer(shimmer)
    )
}

@Composable
private fun FakeDetailUser(
    shimmer: Shimmer,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(dimensionResource(id = R.dimen.size_photo_details_post))
                .clip(CircleShape)
                .myShimmer(shimmer),
        )
        Box(
            modifier = Modifier
                .width(150.dp)
                .height(20.dp)
                .clip(RoundedCornerShape(5.dp))
                .myShimmer(shimmer)
        )
    }
}

@Composable
private fun FakeInfoBlog(
    shimmer: Shimmer,
    modifier: Modifier = Modifier
) {
    val width = remember {
        (100..250).random()
    }
    Box(
        modifier = modifier
            .width(width.dp)
            .height(20.dp)
            .clip(RoundedCornerShape(5.dp))
            .myShimmer(shimmer)
    )

}