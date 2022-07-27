package com.nullpointer.blogcompose.ui.share

import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.nullpointer.blogcompose.R

@Composable
fun ToolbarBack(title: String, actionBack: () -> Unit) {
    TopAppBar(
        backgroundColor = MaterialTheme.colors.primary,
        title = { Text(title) },
        contentColor = Color.White,
        navigationIcon = {
            IconButton(onClick =actionBack) {
                Icon(painterResource(id = R.drawable.ic_arrow_back),
                    stringResource(id = R.string.description_arrow_back))
            }
        })
}

@Composable
fun SimpleToolbar(title: String) {
    TopAppBar(
        contentColor = Color.White,
        backgroundColor = MaterialTheme.colors.primary,
        title = { Text(title) })
}