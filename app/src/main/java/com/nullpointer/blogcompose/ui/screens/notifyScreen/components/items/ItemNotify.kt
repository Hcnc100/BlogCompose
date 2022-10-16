package com.nullpointer.blogcompose.ui.screens.notifyScreen.components.items

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nullpointer.blogcompose.R
import com.nullpointer.blogcompose.core.utils.TimeUtils
import com.nullpointer.blogcompose.models.notify.Notify
import com.nullpointer.blogcompose.models.notify.TypeNotify
import com.nullpointer.blogcompose.ui.share.SimpleImage

@Composable
fun ItemNotify(
    notify: Notify,
    modifier: Modifier = Modifier,
    actionClick: (notify: Notify) -> Unit,
) {
    val colorBackground by animateColorAsState(
        targetValue = if (notify.isOpen) MaterialTheme.colors.surface else MaterialTheme.colors.primary
    )

    Surface(
        elevation = 2.dp,
        color = colorBackground,
        shape = RoundedCornerShape(10.dp),
        modifier = modifier.clickable(onClick = { actionClick(notify) })
    ) {
        Row(
            modifier = Modifier.padding(10.dp)
        ) {
            ImageIconNotify(
                urlImg = notify.userInNotify?.urlImg.toString(),
                type = notify.type,
                modifier = Modifier.weight(2f)
            )


            TextNotifyInfo(
                modifier = Modifier.weight(5f),
                nameLiked = notify.userInNotify?.name.toString(),
                timeStamp = notify.timestamp?.time ?: 0,
                typeNotify = notify.type
            )

            SimpleImage(
                image = notify.urlImgPost,
                sizeImage = 60.dp,
                modifier = Modifier
                    .weight(2f)
                    .align(Alignment.CenterVertically),
                isCircular = false
            )
        }

    }

}

@Composable
private fun ImageIconNotify(
    urlImg: String,
    type: TypeNotify,
    modifier: Modifier = Modifier
) {
    val backgroundColorIcon = remember(type) {
        when (type) {
            TypeNotify.LIKE -> Color.Red
            TypeNotify.COMMENT -> Color.DarkGray
        }
    }
    val iconNotify = remember(type) {
        when (type) {
            TypeNotify.LIKE -> R.drawable.ic_fav
            TypeNotify.COMMENT -> R.drawable.ic_comment
        }
    }

    Box(modifier = modifier) {
        SimpleImage(
            image = urlImg,
            contentDescription = stringResource(R.string.description_user_notify),
            placeholder = R.drawable.ic_person,
            error = R.drawable.ic_person,
            sizeImage = 70.dp,
            isCircular = true
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 5.dp)
                .size(22.dp)
                .drawBehind { drawCircle(backgroundColorIcon) },
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = iconNotify),
                contentDescription = stringResource(R.string.description_icon_notify_indicate),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(3.dp)
            )
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
    val context = LocalContext.current
    val tileNotify = remember(typeNotify) {
        when (typeNotify) {
            TypeNotify.LIKE -> context.getString(R.string.message_notify_liked, nameLiked)
            TypeNotify.COMMENT -> context.getString(R.string.message_notify_comment, nameLiked)
        }
    }
    val timeAgo = remember(timeStamp) {
        TimeUtils.getTimeAgo(timeStamp, context)
    }

    Column(modifier = modifier) {
        Text(
            text = tileNotify,
            style = MaterialTheme.typography.body2,
            fontSize = 13.sp,
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = timeAgo,
            style = MaterialTheme.typography.caption
        )
    }
}
