package com.nullpointer.blogcompose.ui.screens.details.componets.items.post

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nullpointer.blogcompose.R
import com.nullpointer.blogcompose.models.posts.Post
import com.nullpointer.blogcompose.ui.share.SimpleImage


@Composable
fun SuccessDetailsPost(
    blog: Post,
    modifier: Modifier = Modifier,
    actionLike: () -> Unit
) {
    BoxWithConstraints {
        val width = this.maxWidth
        Column(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            HeaderDetails(
                imageBlog = blog.urlImage,
                nameOwnerPost = blog.userPoster?.name.toString()
            )
            Text(text = blog.description, modifier = Modifier.padding(horizontal = 10.dp))
            SimpleImage(
                sizeImage = width,
                image = blog.urlImage,
                contentDescription = stringResource(id = R.string.description_img_post),
                isCircular = false
            )
            ActionDetailsBlog(
                isLiked = blog.ownerLike,
                numberLikes = blog.numberLikes,
                numberComments = blog.numberComments,
                actionLike = actionLike
            )
        }
    }

}


@Composable
private fun ActionDetailsBlog(
    isLiked: Boolean,
    numberLikes: Int,
    numberComments: Int,
    actionLike: () -> Unit,
    modifier: Modifier = Modifier
) {

    val iconLike = remember(isLiked) {
        if (isLiked) R.drawable.ic_fav else R.drawable.ic_unfav
    }

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable { actionLike() }
                .padding(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                painter = painterResource(id = iconLike),
                contentDescription = stringResource(id = R.string.description_like_button)
            )
            Text(
                text = stringResource(id = R.string.text_count_likes, numberLikes)
            )
        }
        Text(
            text = stringResource(id = R.string.text_count_comments, numberComments),
            modifier = Modifier.padding(10.dp)
        )
    }
}

@Composable
private fun HeaderDetails(
    imageBlog: String,
    nameOwnerPost: String,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        SimpleImage(
            image = imageBlog,
            contentDescription = "",
            isCircular = true,
            sizeImage = dimensionResource(id = R.dimen.size_photo_details_post),
            placeholder = R.drawable.ic_person
        )
        Text(text = nameOwnerPost)
    }
}