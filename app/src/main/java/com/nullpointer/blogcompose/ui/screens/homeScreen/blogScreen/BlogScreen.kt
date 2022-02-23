package com.nullpointer.blogcompose.ui.screens.homeScreen.blogScreen

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import com.nullpointer.blogcompose.R
import com.nullpointer.blogcompose.ui.screens.homeScreen.blogScreen.componets.BlogItem

@Composable
fun BlogScreen() {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { /*TODO*/ }) {
                Icon(painterResource(id = R.drawable.ic_add), "")
            }
        }
    ) {
        val listState = rememberLazyListState()
        LazyColumn(state = listState) {
            item { BlogItem() }
            item { BlogItem() }

        }
    }
}