package com.nullpointer.blogcompose.ui.screens.details.componets

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nullpointer.blogcompose.models.posts.Post
import com.valentinilk.shimmer.shimmer


@Composable
fun LoadingOnlyComments(
    post:Post,
    modifier: Modifier = Modifier,
    actionLike:()->Unit
) {
    LazyColumn(modifier = modifier) {
        item(key = { post.id }) {
            HeaderBlogDetails(
                blog = post,
                actionLike = actionLike
            )
        }
        items(10, key = { it }) {
            FakeItemBlog()
        }
    }
}

@Composable
fun LoadingFullPostDetails(
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier) {
        item(key = "-1") {
            FakeHeaderBlogDetails()
        }
        items(10, key = { it }) {
            FakeItemBlog()
        }
    }
}


@Composable
private fun FakeItemBlog() {

    val widthRandom = remember{
        (60..280).random()
    }
    val heightRandom = remember {
        (70..100).random()
    }
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
private fun FakeHeaderBlogDetails() {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
    ) {
        FakeInfoUser()
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
private fun FakeInfoUser(modifier: Modifier = Modifier) {
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