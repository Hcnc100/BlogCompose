package com.nullpointer.blogcompose.ui.screens.notifyScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.nullpointer.blogcompose.R
import com.nullpointer.blogcompose.core.utils.TimeUtils
import com.nullpointer.blogcompose.models.notify.Notify
import com.nullpointer.blogcompose.models.notify.TypeNotify

@Composable
fun ItemNotify(
    notify: Notify,
    modifier: Modifier = Modifier,
    actionClick: (notify: Notify) -> Unit,
) {
    ContainerNotify(
        modifier = modifier,
        notifyIsOpen = notify.isOpen,
        actionClick = { actionClick(notify) }) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {

            ImageIconNotify(
                urlImg = notify.userInNotify?.urlImg.toString(),
                type = notify.type,
                modifier = Modifier.weight(2f)
            )

            Spacer(modifier = Modifier.width(10.dp))

            TextNotifyInfo(
                modifier = Modifier.weight(5f),
                nameLiked = notify.userInNotify?.name.toString(),
                timeStamp = notify.timestamp?.time ?: 0,
                typeNotify = notify.type
            )

            Spacer(modifier = Modifier.width(10.dp)) 
            ImagePostNotify(
                urlImgPost = notify.urlImgPost,
                modifier = Modifier
                    .size(60.dp)
                    .weight(2f)
                    .align(Alignment.CenterVertically)
            )
        }
    }
}

@Composable
private fun ImagePostNotify(
    urlImgPost: String,
    modifier: Modifier = Modifier
) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current).data(urlImgPost).crossfade(true).build(),
        contentDescription = stringResource(R.string.description_post_img_notify),
        placeholder = painterResource(id = R.drawable.ic_image),
        error = painterResource(id = R.drawable.ic_broken_image),
        modifier = modifier
    )
}

@Composable
private fun ImageIconNotify(
    urlImg: String,
    type: TypeNotify,
    modifier: Modifier = Modifier
) {
    Box(modifier=modifier,) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current).data(urlImg)
                .transformations(CircleCropTransformation()).crossfade(true).build(),
            contentDescription = stringResource(R.string.description_user_notify),
            placeholder = painterResource(id = R.drawable.ic_person),
            error = painterResource(id = R.drawable.ic_person),
            modifier = Modifier.size(60.dp),
            contentScale = ContentScale.Crop
        )
        Card(
            backgroundColor = when (type) {
                TypeNotify.LIKE -> Color.Red
                TypeNotify.COMMENT -> Color.DarkGray
            },
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(22.dp)
        ) {
            Image(
                painter = painterResource(
                    id = when (type) {
                        TypeNotify.LIKE -> R.drawable.ic_fav
                        TypeNotify.COMMENT -> R.drawable.ic_comment
                    }
                ),
                contentDescription = stringResource(R.string.description_icon_notify_indicate),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(3.dp)
            )
        }

    }
}

@Composable
private fun ContainerNotify(
    notifyIsOpen: Boolean,
    modifier: Modifier = Modifier,
    actionClick: () -> Unit,
    content: @Composable () -> Unit,
) {
    Card(modifier = modifier
        .clickable { actionClick() }
        .padding(5.dp),
        shape = RoundedCornerShape(10.dp)) {
        // * container background color
        Box(
            modifier = Modifier
                .background(
                    if (notifyIsOpen) Color.Transparent else MaterialTheme.colors.primary.copy(alpha = 0.5f)
                )
                .padding(10.dp)
        ) {
            content()
        }
    }
}

@Composable
private fun TextNotifyInfo(
    modifier: Modifier,
    nameLiked: String,
    timeStamp: Long,
    typeNotify: TypeNotify,
) {
    val textNotify = when (typeNotify) {
        TypeNotify.LIKE -> stringResource(id = R.string.message_notify_liked, nameLiked)
        TypeNotify.COMMENT -> stringResource(id = R.string.message_notify_comment, nameLiked)
    }
    val context = LocalContext.current
    Column(modifier = modifier) {
        Text(
            text = textNotify,
            style = MaterialTheme.typography.body2,
            fontSize = 13.sp,
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = TimeUtils.getTimeAgo(timeStamp, context),
            style = MaterialTheme.typography.caption
        )
    }
}
