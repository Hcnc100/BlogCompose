package com.nullpointer.blogcompose.ui.screens.homeScreen.blogScreen.componets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.nullpointer.blogcompose.R

@Composable
fun ButtonsInteractionBlog() {
    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {

        Row {
            IconButton(onClick = { /*TODO*/ }) {
                Icon(painterResource(id = R.drawable.ic_fav), "")
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