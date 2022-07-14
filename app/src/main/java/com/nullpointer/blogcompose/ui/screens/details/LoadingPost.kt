package com.nullpointer.blogcompose.ui.screens.details

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.valentinilk.shimmer.shimmer

@Composable
fun LoadingPost(
    headerLoading: @Composable (() -> Unit)? = null
) {
    LazyColumn {
        item {
            headerLoading?.invoke() ?: HeaderBlogDetails()
        }
        items(10) {
            val widthRandom = (60..280).random()
            val heightRandom = (70..100).random()
            ItemBlog(widthRandom = widthRandom, heightRandom = heightRandom)
        }
    }
}


@Composable
private fun ItemBlog(widthRandom: Int, heightRandom: Int) {
    Row(modifier = Modifier.padding(10.dp)) {
        Card(
            modifier = Modifier
                .size(40.dp)
                .shimmer(), shape = CircleShape
        ) {}
        Spacer(modifier = Modifier.width(10.dp))
        Card(
            modifier = Modifier
                .width(widthRandom.dp)
                .height(heightRandom.dp)
                .shimmer(),
            shape = RoundedCornerShape(10.dp)
        ) {}
    }

}


@Composable
private fun HeaderBlogDetails() {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
    ) {
        InfoUser()
        Card(
            modifier = Modifier
                .height(250.dp)
                .aspectRatio(.80f)
                .shimmer()
                .align(CenterHorizontally),
            shape = RoundedCornerShape(5.dp)
        ) {}
    }
}

@Composable
private fun InfoUser(modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(10.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Card(
                modifier = Modifier
                    .size(50.dp)
                    .shimmer(),
                shape = CircleShape
            ) {}
            Spacer(modifier = Modifier.width(10.dp))
            Card(
                modifier = Modifier
                    .width(50.dp)
                    .height(20.dp)
                    .shimmer()
            ) {}
        }
        Spacer(modifier = Modifier.height(10.dp))
        Card(
            modifier = Modifier
                .width(180.dp)
                .height(20.dp)
                .shimmer()
        ) {}
    }
}