package com.nullpointer.blogcompose.ui.screens.addPost

import android.widget.Space
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter
import com.google.accompanist.insets.navigationBarsWithImePadding
import com.nullpointer.blogcompose.R

@Composable
fun AddBlogScreen() {

    Scaffold(topBar = {
        TopAppBar(title = { Text("Nuevo post") },
            navigationIcon = { Icon(painterResource(id = R.drawable.ic_arrow_back), "") })
    }, floatingActionButton = {
        ExtendedFloatingActionButton(text = { Text("Publicar") },
            onClick = { /*TODO*/ })

    }) {
        Column(modifier = Modifier
            .verticalScroll(rememberScrollState())) {
            ImageNewBlog()
            Spacer(modifier = Modifier.height(10.dp))
            DescriptionNewBlog()
        }
    }

}

@Composable
fun DescriptionNewBlog() {
    val (description, changeDescription) = rememberSaveable {
        mutableStateOf("")
    }
    val maxLength = 250
    val hasError = false
    Column(modifier = Modifier
        .padding(10.dp)
        .fillMaxWidth()) {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            value = description,
            onValueChange = { if (it.length <= maxLength) changeDescription(it) },
            label = { Text("Descirpcion") },
            placeholder = { Text("Descripcion del blog") },
            shape = RoundedCornerShape(20.dp)
        )
        Text(modifier = Modifier
            .align(Alignment.End)
            .padding(horizontal = 10.dp, vertical = 5.dp),
            text = if (hasError) "Error" else "${description.length}/$maxLength",
            style = MaterialTheme.typography.caption,
            color = if (hasError) MaterialTheme.colors.error else MaterialTheme.colors.onBackground
        )
    }

}

@Composable
fun ImageNewBlog() {
    Card(shape = RoundedCornerShape(10.dp), modifier = Modifier.padding(10.dp)) {
        Box(contentAlignment = Alignment.Center) {
            val painter = rememberImagePainter(data = "https://picsum.photos/200") {
                crossfade(true)
            }
            val state = painter.state
            Image(painter = if (state !is ImagePainter.State.Error) painter else painterResource(
                id = R.drawable.ic_broken_image),
                contentDescription = "",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f))
            if (state is ImagePainter.State.Loading) {
                CircularProgressIndicator()
            }

            FloatingActionButton(
                onClick = { /*TODO*/ },
                modifier = Modifier
                    .padding(10.dp)
                    .size(40.dp)
                    .align(Alignment.BottomEnd)
            ) {
                Icon(painterResource(id = R.drawable.ic_edit), "")
            }
        }
    }
}
