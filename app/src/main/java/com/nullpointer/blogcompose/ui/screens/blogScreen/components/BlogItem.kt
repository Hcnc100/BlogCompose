package com.nullpointer.blogcompose.ui.screens.blogScreen.components

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.nullpointer.blogcompose.R
import com.nullpointer.blogcompose.core.utils.TimeUtils
import com.nullpointer.blogcompose.ui.screens.blogScreen.ActionsPost
import com.nullpointer.blogcompose.models.posts.SimplePost
import java.util.*

@Composable
fun BlogItem(
    post: SimplePost,
    actionBlog: (ActionsPost, SimplePost) -> Unit,
    modifier: Modifier = Modifier
) {
    ContainerBlog(modifier=modifier) {
        Column {
            HeaderOwnerBlog(
                urlImg = post.userPoster?.urlImg.toString(),
                name = post.userPoster?.name.toString(),
                modifier = Modifier.padding(10.dp)
            )
            ImageBlog(urlImg = post.urlImage) {
                actionBlog(ActionsPost.DETAILS, post)
            }
            ActionsPost(
                post = post,
                actionBlog = actionBlog,
                modifier = Modifier.padding(5.dp)
            )
            TextLikes(
                numberComments = post.numberComments,
                numberLikes = post.numberLikes,
                modifier = Modifier.padding(5.dp)
            )
            DescriptionBlog(
                description = post.description,
                modifier = Modifier.padding(5.dp)
            )
            TextTime(
                timeStamp = post.timestamp,
                modifier = Modifier.padding(5.dp)
            )
        }
    }
}

@Composable
private fun ContainerBlog(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Card(
        modifier = modifier
            .padding(10.dp)
            .wrapContentWidth(),
        shape = RoundedCornerShape(10.dp),
        content = content
    )
}

@Composable
private fun ActionsPost(
    modifier: Modifier = Modifier,
    post: SimplePost,
    actionBlog: (ActionsPost, SimplePost) -> Unit
) {
    val iconLike = derivedStateOf {
        if (post.ownerLike) R.drawable.ic_fav else R.drawable.ic_unfav
    }
    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Row {
            IconAction(
                drawableRes = iconLike.value,
                stringDescription = R.string.description_like_button
            ) {
                actionBlog(ActionsPost.LIKE, post)
            }
            IconAction(
                drawableRes = R.drawable.ic_comment,
                stringDescription = R.string.description_to_comment
            ) {
                actionBlog(ActionsPost.COMMENT, post)
            }
            IconAction(
                drawableRes = R.drawable.ic_share,
                stringDescription = R.string.description_share_post
            ) {
                actionBlog(ActionsPost.SHARE, post)
            }
        }

        Row {
            IconAction(
                drawableRes = R.drawable.ic_download,
                stringDescription = R.string.description_download_post
            ) {
                actionBlog(ActionsPost.DOWNLOAD, post)
            }
            IconAction(
                drawableRes = R.drawable.ic_save,
                stringDescription = R.string.description_save_post
            ) {
                actionBlog(ActionsPost.SAVE, post)
            }
        }
    }
}

@Composable
private fun IconAction(
    @DrawableRes drawableRes: Int,
    @StringRes stringDescription: Int,
    modifier: Modifier = Modifier,
    action: () -> Unit,
) {
    Icon(
        painter = painterResource(id = drawableRes),
        contentDescription = stringResource(id = stringDescription),
        modifier = modifier
            .clip(CircleShape)
            .clickable { action() }
            .padding(7.dp))
}


@Composable
private fun ImageBlog(
    urlImg: String,
    actionClick: () -> Unit
) {
    AsyncImage(
        contentScale = ContentScale.Crop,
        model = urlImg,
        contentDescription = stringResource(id = R.string.description_img_blog),
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable { actionClick() }
    )
}

@Composable
private fun HeaderOwnerBlog(
    urlImg: String,
    name: String,
    modifier: Modifier = Modifier
) {
    Row(verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(urlImg)
                .crossfade(true)
                .transformations(CircleCropTransformation())
                .build(),
            contentDescription = stringResource(id = R.string.description_img_owner_post),
            modifier = Modifier.size(50.dp),
            placeholder = painterResource(id = R.drawable.ic_person)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = name,
            style = MaterialTheme.typography.body1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun TextTime(timeStamp: Date?, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val textTime= remember {
        TimeUtils.getTimeAgo(timeStamp?.time ?: 0, context)
    }
    Text(
        text = textTime,
        style = MaterialTheme.typography.caption,
        modifier = modifier
    )
}


@Composable
private fun DescriptionBlog(
    modifier: Modifier = Modifier,
    description: String,
) {
    // * folding text to description post
    val (isExpanded, changeExpanded) = rememberSaveable { mutableStateOf(false) }
    Text(
        text = description,
        maxLines = if (isExpanded) Int.MAX_VALUE else 2,
        overflow = if (isExpanded) TextOverflow.Visible else TextOverflow.Ellipsis,
        modifier = modifier.clickable { changeExpanded(!isExpanded) },
        style = MaterialTheme.typography.body1
    )
}


@Composable
fun TextLikes(
    numberLikes: Int,
    numberComments: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = stringResource(id = R.string.text_count_likes, numberLikes),
            modifier = Modifier.padding(vertical = 5.dp),
            style = MaterialTheme.typography.caption,
            fontWeight = FontWeight.W400,
            fontSize = 14.sp
        )
        Text(
            text = stringResource(id = R.string.text_count_comments, numberComments),
            modifier = Modifier.padding(vertical = 5.dp),
            style = MaterialTheme.typography.caption,
            fontWeight = FontWeight.W400,
            fontSize = 14.sp
        )
    }
}



