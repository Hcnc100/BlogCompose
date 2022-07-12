package com.nullpointer.blogcompose.ui.screens.notifyScreen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.valentinilk.shimmer.shimmer

@Composable
fun LoadingNotify() {
    LazyColumn {
        items(10) {
            val widthNameRandom = (50..280).random()
            Card(modifier = Modifier.padding(5.dp)) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ImageProfile()
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        NameTextFake(widthNameRandom = widthNameRandom)
                        Spacer(modifier = Modifier.height(10.dp))
                        FakeTextLoading()
                    }
                }
            }
        }
    }
}

@Composable
private fun FakeTextLoading() {
    Column(modifier = Modifier.fillMaxWidth()) {
        repeat(2) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(15.dp)
                    .shimmer(),
                shape = RoundedCornerShape(3.dp)
            ) {}
            Spacer(modifier = Modifier.height(5.dp))
        }
    }
}


@Composable
private fun NameTextFake(widthNameRandom: Int) {
    Row {
        Card(
            modifier = Modifier
                .width(widthNameRandom.dp)
                .height(20.dp)
                .shimmer()
        ) {}
    }
}

@Composable
private fun ImageProfile() {
    Card(
        modifier = Modifier
            .size(50.dp)
            .shimmer(),
        shape = CircleShape
    ) {}
}