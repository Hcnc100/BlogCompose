package com.nullpointer.blogcompose.ui.screens.blogScreen.components.items

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
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
fun BlogLoadItem(
    shimmer: Shimmer,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            HeaderLoading(shimmer = shimmer, modifier = Modifier.padding(5.dp))
            ImageLoading(shimmer = shimmer)
            FakeTextLoading(shimmer = shimmer, modifier = Modifier.padding(5.dp))
        }
    }
}

@Composable
private fun FakeTextLoading(
    shimmer: Shimmer,
    modifier: Modifier = Modifier
) {
    val numberCommentsFake = remember {
        (1..3).random()
    }
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        repeat(numberCommentsFake) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .myShimmer(shimmer)
            )
        }
    }
}

@Composable
private fun ImageLoading(modifier: Modifier = Modifier, shimmer: Shimmer) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .myShimmer(shimmer)
    )
}

@Composable
private fun HeaderLoading(modifier: Modifier = Modifier, shimmer: Shimmer) {
    val widthRandom = remember {
        (50..280).random()
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(dimensionResource(id = R.dimen.size_photo_owner_blog))
                .clip(CircleShape)
                .myShimmer(shimmer)
        )
        Box(
            modifier = Modifier
                .width(widthRandom.dp)
                .height(20.dp)
                .clip(RoundedCornerShape(5.dp))
                .myShimmer(shimmer),
        )
    }
}