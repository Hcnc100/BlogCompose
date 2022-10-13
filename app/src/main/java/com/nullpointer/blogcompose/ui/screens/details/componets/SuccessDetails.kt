package com.nullpointer.blogcompose.ui.screens.details.componets

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.nullpointer.blogcompose.R
import com.nullpointer.blogcompose.models.posts.Post


@Composable
fun ImageProfileUser(
    urlImg: String,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    AsyncImage(
        model = ImageRequest
            .Builder(LocalContext.current)
            .crossfade(true).transformations(CircleCropTransformation())
            .data(urlImg).build(),
        contentDescription = contentDescription,
        placeholder = painterResource(id = R.drawable.ic_person),
        error = painterResource(id = R.drawable.ic_person),
        modifier = modifier
    )
}

@Composable
fun HeaderBlogDetails(
    blog: Post,
    modifier: Modifier = Modifier,
    actionLike: () -> Unit
) {

    val iconLike by remember(blog.ownerLike) {
        derivedStateOf {
            if (blog.ownerLike) R.drawable.ic_fav else R.drawable.ic_unfav
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(10.dp)
        ) {
            ImageProfileUser(
                urlImg = blog.userPoster?.urlImg ?: "",
                contentDescription = "",
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = blog.userPoster?.name ?: "")
        }
        Text(text = blog.description, modifier = Modifier.padding(10.dp))
        Spacer(modifier = Modifier.height(10.dp))
        AsyncImage(
            model = blog.urlImage,
            contentDescription = stringResource(id = R.string.description_img_post),
            modifier = Modifier
                .fillMaxWidth()
        )
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Row(modifier = Modifier
                .padding(10.dp)
                .clickable {
                    actionLike()
                }, verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = iconLike),
                    contentDescription = stringResource(id = R.string.description_like_button)
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = stringResource(id = R.string.text_count_likes, blog.numberLikes)
                )
            }
            Text(
                text = stringResource(id = R.string.text_count_comments, blog.numberComments),
                modifier = Modifier.padding(10.dp)
            )
        }

    }
}
