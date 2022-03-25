package com.nullpointer.blogcompose.ui.screens.details.componets

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.nullpointer.blogcompose.R
import com.nullpointer.blogcompose.core.states.Resource
import com.nullpointer.blogcompose.models.Post
import com.nullpointer.blogcompose.ui.share.ImageProfile


@Composable
fun ImagePost(
    statePost: Resource<Post>,
    actionLike: (Boolean) -> Unit,
) {
    when (statePost) {
        is Resource.Failure -> {}
        is Resource.Loading -> Box(modifier = Modifier
            .fillMaxWidth()
            .height(250.dp), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        is Resource.Success -> HeaderBlog(statePost.data, actionLike = actionLike)
    }
}


@Composable
fun HeaderBlog(post: Post, actionLike: (Boolean) -> Unit) {
    // * image post and number like and comments
    val painter = rememberImagePainter(post.urlImage)
    Column(modifier = Modifier.padding(10.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            ImageProfile(urlImgProfile = post.poster?.urlImg.toString(),
                paddingLoading = 5.dp,
                sizeImage = 30.dp)
            Spacer(modifier = Modifier.width(15.dp))
            Text(post.poster?.name.toString(),
                style = MaterialTheme.typography.body1,
                fontWeight = FontWeight.W600)
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(text = post.description, modifier = Modifier.padding(10.dp))
        Image(painter = painter,
            contentDescription = "",
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp))
        InfoPost(post = post, actionLike)
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun InfoPost(post: Post, actionLike: (Boolean) -> Unit) {

    // * when is in realtime so need mutable state
    var likeState by remember { mutableStateOf(post.ownerLike) }
    var numberLike by remember { mutableStateOf(post.numberLikes) }

    Row(horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)) {
        // * animate content that can change
        AnimatedContent(targetState = likeState) {
            // * button like and number likes
            Row(modifier = Modifier.clickable {
                actionLike(!likeState)
                likeState = !likeState
                if (likeState) numberLike += 1 else numberLike -= 1
            }) {
                Icon(painterResource(
                    id = if (likeState) R.drawable.ic_fav else R.drawable.ic_unfav),
                    contentDescription = "")
                Spacer(modifier = Modifier.width(10.dp))
                Text("$numberLike likes")
            }
        }
        // * info comments
        Text("${post.numberComments} comentarios")
    }
}