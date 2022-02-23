package com.nullpointer.blogcompose.ui.screens.blogScreen

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable

@Composable
fun BlogScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hola") }
            )
        }
    ) {
        LazyColumn(){
            item { BlogItem() }
            item { BlogItem() }
        }
    }
}