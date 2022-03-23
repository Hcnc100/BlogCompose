package com.nullpointer.blogcompose.ui.screens.details.componets

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import com.nullpointer.blogcompose.core.utils.TimeUtils
import com.nullpointer.blogcompose.models.Comment
import com.valentinilk.shimmer.shimmer


@Composable
fun Comments(
    comment: Comment? = null,
) {
    Row {
        ImageComment(comment?.urlImg)
        Column {
            ContentComment(comment)
            TimeComment(comment?.timestamp?.time)
        }
    }
}

@Composable
fun ContentComment(
    comment: Comment? = null,
) {
    val modifierComment = if (comment != null) Modifier.padding(10.dp) else Modifier
        .padding(10.dp)
        .width((100..350).random().dp)
        .height((50..150).random().dp)
        .shimmer()
    Card(shape = RoundedCornerShape(10.dp),
        modifier = modifierComment.padding(horizontal = 10.dp)) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(comment?.nameUser ?: "",
                style = MaterialTheme.typography.subtitle2,
                fontWeight = FontWeight.W600)
            Spacer(modifier = Modifier.height(5.dp))
            Text(comment?.comment ?: "")
        }
    }
}

@Composable
fun TimeComment(timeComment: Long?) {
    if (timeComment != null) {
        val context = LocalContext.current
        Text(TimeUtils.getTimeAgo(timeComment, context),
            style = MaterialTheme.typography.caption,
            modifier = Modifier.padding(horizontal = 20.dp))
    }
}

@Composable
fun ImageComment(
    urlImgProfile: String? = null,
) {
    val modifierImg = Modifier
        .padding(start = 10.dp, top = 10.dp)
        .size(40.dp)
    if (urlImgProfile != null) {
        val painter = rememberImagePainter(urlImgProfile) {
            transformations(CircleCropTransformation())
        }
        Image(painter = painter,
            contentDescription = "",
            modifier = modifierImg)
    } else {
        Box(modifier = modifierImg
            .shimmer()
            .clip(CircleShape))
    }
}