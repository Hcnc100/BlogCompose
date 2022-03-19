package com.nullpointer.blogcompose.ui.screens.details.componets

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import com.nullpointer.blogcompose.core.utils.TimeUtils


@Composable
fun Comments(
    urlImg: String,
    nameUser: String,
    timestamp: Long,
    comment: String,
) {
    val context = LocalContext.current
    Row(modifier = Modifier.padding(10.dp)) {
        val painter = rememberImagePainter(urlImg) {
            transformations(CircleCropTransformation())
        }
        Image(painter = painter, contentDescription = "", modifier = Modifier
            .size(30.dp)
        )
        Column {
            Card(shape = RoundedCornerShape(10.dp),
                modifier = Modifier.padding(bottom = 5.dp, start = 10.dp, end = 10.dp)) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(nameUser,
                        style = MaterialTheme.typography.subtitle2,
                        fontWeight = FontWeight.W600)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(comment)
                }
            }
            Text(TimeUtils.getTimeAgo(timestamp, context),
                modifier = Modifier.padding(start = 10.dp),
                style = MaterialTheme.typography.caption)
        }

    }
}