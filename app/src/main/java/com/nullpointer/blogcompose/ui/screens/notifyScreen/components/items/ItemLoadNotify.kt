package com.nullpointer.blogcompose.ui.screens.notifyScreen.components.items

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
fun ItemLoadNotify(
    shimmer: Shimmer,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ImageProfile(shimmer)
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                NameTextFake(shimmer)
                FakeTextLoading(shimmer)
            }
        }
    }
}

@Composable
private fun FakeTextLoading(
    shimmer: Shimmer,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        repeat(2) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(15.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .myShimmer(shimmer),
            )
        }
    }
}


@Composable
private fun NameTextFake(
    shimmer: Shimmer,
    modifier: Modifier = Modifier
) {
    val widthNameRandom = remember {
        (50..280).random()
    }
    Box(
        modifier = modifier
            .width(widthNameRandom.dp)
            .height(20.dp)
            .myShimmer(shimmer)
    )

}

@Composable
private fun ImageProfile(
    shimmer: Shimmer,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(dimensionResource(id = R.dimen.size_photo_notify))
            .clip(CircleShape)
            .myShimmer(shimmer)
    )
}