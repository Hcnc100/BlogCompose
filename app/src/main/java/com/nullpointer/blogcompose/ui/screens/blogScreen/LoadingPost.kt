package com.nullpointer.blogcompose.ui.screens.blogScreen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.valentinilk.shimmer.shimmer

@Composable
fun LoadingPost() {
    LazyColumn{
        items(5){
            Card(
                modifier = Modifier.padding(10.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                val widthRandom=(50..280).random()
                Column(
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth()
                ) {
                    HeaderLoading(widthRandom=widthRandom)
                    Spacer(modifier = Modifier.padding(10.dp))
                    ImageLoading()
                    Spacer(modifier = Modifier.padding(10.dp))
                    FakeTextLoading()
                }
            }
        }
    }
}

@Composable
private fun FakeTextLoading() {
    Column(modifier = Modifier.fillMaxWidth()) {
        repeat((1..3).random()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp)
                    .shimmer(),
                shape = RoundedCornerShape(3.dp)
            ) {}
            Spacer(modifier = Modifier.height(5.dp))
        }
    }
}

@Composable
private fun ImageLoading(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .shimmer()
    ) {}
}

@Composable
private fun HeaderLoading(modifier: Modifier = Modifier,widthRandom:Int) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier) {
        Card(
            modifier = Modifier
                .size(45.dp)
                .shimmer(),
            shape = CircleShape
        ) {}
        Spacer(modifier = Modifier.padding(10.dp))
        Card(
            modifier = Modifier
                .width(widthRandom.dp)
                .height(40.dp)
                .shimmer(),
            shape = RoundedCornerShape(5.dp)
        ) {}
    }
}