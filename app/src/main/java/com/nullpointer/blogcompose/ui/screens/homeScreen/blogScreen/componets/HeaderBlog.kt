package com.nullpointer.blogcompose.ui.screens.homeScreen.blogScreen.componets

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import com.nullpointer.blogcompose.R

@Composable
fun HeaderBlog() {

    Row(
        modifier = Modifier.padding(7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val painter = rememberImagePainter(
            data = "https://picsum.photos/200", builder = {
                transformations(CircleCropTransformation())
                crossfade(true)
                placeholder(R.drawable.ic_account)
            }
        )
        Image(painter = painter,
            contentDescription = "Imagen del usuario",
            modifier = Modifier
                .size(40.dp)
                .weight(1f)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(text = "Hola como estas este es un nombre de usuario muy largo y no va a caber",
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(5f),
            style = MaterialTheme.typography.body1,
            fontWeight = FontWeight.W600
        )

        IconButton(onClick = { /*TODO*/ }, modifier = Modifier
            .weight(1f)
            .size(20.dp)) {
            Icon(painterResource(id = R.drawable.ic_options_vertical),
                "")
        }
    }
}