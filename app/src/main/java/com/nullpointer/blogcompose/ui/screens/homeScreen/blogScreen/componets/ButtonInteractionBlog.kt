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
import com.nullpointer.blogcompose.R

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ButtonsInteractionBlog(
    ownerLike: Boolean,
    changeLike: (Boolean) -> Unit,
) {

    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {

        Row {
            IconButton(onClick = {
                changeLike(!ownerLike)
            }) {
                AnimatedContent(targetState = ownerLike) {
                    Icon(painterResource(
                        id = if (ownerLike) R.drawable.ic_fav else R.drawable.ic_unfav),
                        contentDescription = "")
                }

            }
            IconButton(onClick = { /*TODO*/ }) {
                Icon(painterResource(id = R.drawable.ic_comment), "")
            }
            IconButton(onClick = { /*TODO*/ }) {
                Icon(painterResource(id = R.drawable.ic_share), "")
            }
        }




        Row {
            IconButton(onClick = { /*TODO*/ }) {
                Icon(painterResource(id = R.drawable.ic_download), "")
            }
            IconButton(onClick = { /*TODO*/ }) {
                Icon(painterResource(id = R.drawable.ic_bookmark), "")
            }
        }


    }

}