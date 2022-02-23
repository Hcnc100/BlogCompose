package com.nullpointer.blogcompose.ui.screens.homeScreen.blogScreen

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import com.nullpointer.blogcompose.ui.screens.homeScreen.blogScreen.componets.BlogItem

@Composable
fun BlogScreen() {
    Scaffold {
        val listState= rememberLazyListState()
        LazyColumn(state = listState){
            item { BlogItem() }
            item { BlogItem() }

        }
    }
}