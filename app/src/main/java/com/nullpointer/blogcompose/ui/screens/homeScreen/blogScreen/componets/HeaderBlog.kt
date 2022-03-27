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
import com.nullpointer.blogcompose.ui.share.ImageProfile

@Composable
fun HeaderBlog(
    urlImgOwnerPost: String,
    nameOwnerPost: String,
) {

    Row(
        modifier = Modifier.padding(7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ImageProfile(urlImgProfile = urlImgOwnerPost,
            paddingLoading = 5.dp,
            sizeImage = 40.dp,
            modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.width(10.dp))
        Text(text = nameOwnerPost,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(5f),
            style = MaterialTheme.typography.body1,
            fontWeight = FontWeight.W600
        )
        // ! this options
//        IconButton(onClick = { }, modifier = Modifier
//            .weight(1f)
//            .size(20.dp)) {
//            Icon(painterResource(id = R.drawable.ic_options_vertical),
//                "")
//        }
    }
}