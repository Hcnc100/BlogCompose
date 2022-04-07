package com.nullpointer.blogcompose.ui.screens.homeScreen.blogScreen.componets

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.nullpointer.blogcompose.R

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ButtonsInteractionBlog(
    ownerLike: Boolean,
    actionShare: () -> Unit,
    actionComments: () -> Unit,
    changeLike: (Boolean) -> Unit,
) {

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row {
            // * change with animation, when like post
            IconButton(onClick = { changeLike(!ownerLike) }) {
                AnimatedContent(targetState = ownerLike) {
                    Icon(painterResource(
                        id = if (ownerLike) R.drawable.ic_fav else R.drawable.ic_unfav),
                        contentDescription = stringResource(id = R.string.description_like_button))
                }
            }
            // * when click in this, show keyboard
            IconButton(onClick = { actionComments() }) {
                Icon(painterResource(id = R.drawable.ic_comment),
                    stringResource(R.string.description_to_to_comment))
            }
        }
        Row {
            // ! i don't should add this
//            // * download image post
//            IconButton(onClick = {
//                permissionState.launchPermissionRequest()
//            }) {
//                Icon(painterResource(id = R.drawable.ic_download), "")
//            }
            // * share post
            IconButton(onClick = { actionShare() }) {
                Icon(painterResource(id = R.drawable.ic_share),
                    stringResource(R.string.description_share_post))
            }
        }
    }

}