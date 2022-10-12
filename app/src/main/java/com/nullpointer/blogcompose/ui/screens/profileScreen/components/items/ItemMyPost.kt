package com.nullpointer.blogcompose.ui.screens.profileScreen.components.items

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nullpointer.blogcompose.R
import com.nullpointer.blogcompose.models.posts.MyPost
import com.nullpointer.blogcompose.ui.share.SimpleImage

@Composable
fun ItemMyPost(
    post: MyPost,
    modifier: Modifier = Modifier,
    actionDetails: (String) -> Unit
) {
    Card(
        modifier = modifier
            .size(dimensionResource(id = R.dimen.size_photo_my_post))
            .clickable { actionDetails(post.id) },
        shape = RoundedCornerShape(5.dp)
    ) {
        SimpleImage(
            image = post.urlImage,
            sizePlaceHolder = 50.dp,
            isCircular = false,
            contentDescription = stringResource(id = R.string.description_img_blog),
            sizeImage = dimensionResource(id = R.dimen.size_photo_my_post)
        )
    }
}