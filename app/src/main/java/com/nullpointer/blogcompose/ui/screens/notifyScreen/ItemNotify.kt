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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nullpointer.blogcompose.R
import com.nullpointer.blogcompose.core.utils.TimeUtils
import com.nullpointer.blogcompose.models.notify.Notify
import com.nullpointer.blogcompose.models.notify.TypeNotify
import com.nullpointer.blogcompose.ui.share.ImagePost
import com.nullpointer.blogcompose.ui.share.ImageProfile

@Composable
fun ItemNotify(
    notify: Notify,
    actionClick: (notify: Notify) -> Unit,
) {
    // * change color of items when is open or no
    val modifierColor = Modifier.background(
        if (notify.isOpen) Color.Transparent else MaterialTheme.colors.primary.copy(alpha = 0.5f)
    )
    // * card container
    Card(modifier = Modifier
        .clickable { actionClick(notify) }
        .padding(vertical = 5.dp, horizontal = 5.dp),
        shape = RoundedCornerShape(10.dp)) {
        // * container background color
        Box(modifier = modifierColor) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp)
            ) {

                Box(
                    modifier = Modifier
                        .weight(2f)
                        .size(60.dp),
                ) {
                    ImageProfile(
                        urlImgProfile = notify.userInNotify?.urlImg.toString(),
                        paddingLoading = 10.dp,
                        modifier = Modifier
                            .fillMaxSize(),
                        contentDescription = stringResource(R.string.description_user_notify)
                    )
                    Card(
                        backgroundColor = when (notify.type) {
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
                                id = when (notify.type) {
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

                Spacer(modifier = Modifier.width(10.dp))

                TextNotifyInfo(
                    modifier = Modifier.weight(5f),
                    nameLiked = notify.userInNotify?.name.toString(),
                    timeStamp = notify.timestamp?.time ?: 0,
                    typeNotify = notify.type
                )

                ImagePost(
                    urlImgPost = notify.urlImgPost,
                    paddingLoading = 0.dp,
                    contentDescription = stringResource(R.string.description_post_img_notify),
                    modifier = Modifier
                        .size(60.dp)
                        .weight(2f)
                        .align(Alignment.CenterVertically)
                )
            }
        }
    }
}

@Composable
fun TextNotifyInfo(
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
