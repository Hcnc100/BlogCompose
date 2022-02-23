package com.nullpointer.blogcompose.ui.screens.homeScreen.searchScreen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.GridItemSpan
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.nullpointer.blogcompose.R


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SearchScreen() {
    val listImages = listOf(
        "https://picsum.photos/500",
        "https://picsum.photos/500",
        "https://picsum.photos/500",
        "https://picsum.photos/500",
        "https://picsum.photos/500",
        "https://picsum.photos/500",
        "https://picsum.photos/500",
        "https://picsum.photos/500",
        "https://picsum.photos/500",
        "https://picsum.photos/500",
        "https://picsum.photos/500",
        "https://picsum.photos/500",
        "https://picsum.photos/500",
        "https://picsum.photos/500",
        "https://picsum.photos/500",
        "https://picsum.photos/500",
        "https://picsum.photos/500",
        "https://picsum.photos/500",
        "https://picsum.photos/500",
        "https://picsum.photos/500",
        "https://picsum.photos/500",

        )
    val grisState = rememberLazyGridState()
    Scaffold {
        LazyVerticalGrid(
            cells = GridCells.Adaptive(110.dp),
            contentPadding = PaddingValues(6.dp),
            state = grisState
        ) {
            item(span = { GridItemSpan(Int.MAX_VALUE) }) {
                TextInputSearch()
            }
            items(listImages.size) { index ->
                Card(modifier = Modifier
                    .size(110.dp)
                    .padding(3.dp)) {
                    val painter = rememberImagePainter(data = listImages[index])
                    Image(painter = painter, contentDescription = "",
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }


}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TextInputSearch() {
    val (text, changeText) = rememberSaveable { mutableStateOf("") }
    Box() {
        val keyboardController = LocalSoftwareKeyboardController.current
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp).height(60.dp),
            value = text,
            onValueChange = changeText,
            singleLine = true,
            label = { Text("Buscar") },
            shape = RoundedCornerShape(20.dp),
            leadingIcon = { Icon(painterResource(id = R.drawable.ic_search), "") },
            placeholder = { Text("Escribe algo a buscar") },
            keyboardActions = KeyboardActions(
                onSearch = {
                    keyboardController?.hide()
                }
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Search
            )
        )
    }
}
