package com.nullpointer.blogcompose.ui.screens.details

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.nullpointer.blogcompose.R
import com.nullpointer.blogcompose.models.Comment

@Composable
fun ItemComment(
    comment: Comment,
) {
    Row(modifier = Modifier.padding(10.dp)) {
        AsyncImage(
            model = ImageRequest
                .Builder(LocalContext.current)
                .crossfade(true).transformations(CircleCropTransformation())
                .data(comment.userComment?.urlImg).build(),
            contentDescription = "",
            placeholder = painterResource(id = R.drawable.ic_person),
            error = painterResource(id = R.drawable.ic_person),
            modifier = Modifier.size(40.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Card(shape = RoundedCornerShape(10.dp)) {
            Column(modifier = Modifier.padding(10.dp)) {
                Text(comment.userComment?.name ?: "")
                Spacer(modifier = Modifier.height(5.dp))
                Text(text = comment.comment)
            }
        }
    }
}