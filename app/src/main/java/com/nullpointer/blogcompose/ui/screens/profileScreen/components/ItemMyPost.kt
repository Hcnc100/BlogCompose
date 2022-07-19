package com.nullpointer.blogcompose.ui.screens.profileScreen.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.nullpointer.blogcompose.R
import com.nullpointer.blogcompose.models.posts.MyPost

@Composable
fun ItemMyPost(
    post: MyPost,
    modifier: Modifier = Modifier,
    actionDetails: (String) -> Unit
) {
    Card(
        modifier = modifier
            .padding(4.dp)
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable {
                actionDetails(post.id)
            },
        shape = RoundedCornerShape(5.dp)
    ) {
        AsyncImage(
            model = post.urlImage,
            contentDescription = stringResource(id = R.string.description_img_blog),
            contentScale = ContentScale.Crop
        )
    }
}