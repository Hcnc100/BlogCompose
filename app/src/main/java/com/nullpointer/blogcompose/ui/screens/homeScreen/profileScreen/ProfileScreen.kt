package com.nullpointer.blogcompose.ui.screens.homeScreen.profileScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.annotation.ExperimentalCoilApi
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import com.nullpointer.blogcompose.R
import com.nullpointer.blogcompose.core.states.Resource
import com.nullpointer.blogcompose.presentation.AuthViewModel
import com.nullpointer.blogcompose.presentation.MyPostViewModel
import com.nullpointer.blogcompose.presentation.PostViewModel
import com.nullpointer.blogcompose.ui.screens.swipePosts.ScreenSwiperPost
import kotlinx.coroutines.flow.collect

@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel = hiltViewModel(),
    myPostViewModel: MyPostViewModel = hiltViewModel(),
    postViewModel: PostViewModel = hiltViewModel(),
) {

    val stateListPost = myPostViewModel.listMyPost.collectAsState()
    val stateLoading = myPostViewModel.stateLoad.collectAsState()
    val stateConcatenate = myPostViewModel.stateConcatenate.collectAsState()
    val postMessage = myPostViewModel.messageMyPosts
    val scaffoldState = rememberScaffoldState()
    LaunchedEffect(postMessage) {
        postMessage.collect {
            scaffoldState.snackbarHostState.showSnackbar(it)
        }
    }

    ScreenSwiperPost(resultListPost = stateListPost.value,
        scaffoldState = scaffoldState,
        updateListPost = { myPostViewModel.requestNewPost(true) },
        actionBottomReached = myPostViewModel::concatenatePost,
        actionChangePost = postViewModel::likePost,
        staticInfo = Pair(authViewModel.photoUser, authViewModel.nameUser),
        isLoadNewData = stateLoading.value is Resource.Loading,
        isConcatenateData = stateConcatenate.value is Resource.Loading
    ) {
        HeaderProfile(
            urlImgProfile = authViewModel.photoUser,
            nameProfile = authViewModel.nameUser
        )
    }
}

@Composable
fun HeaderProfile(
    urlImgProfile: String,
    nameProfile: String,
) {
    Box {
        InfoProfile(urlImgProfile, nameProfile)
        IconButton(onClick = { /*TODO*/ }, modifier = Modifier
            .align(Alignment.TopEnd)
            .padding(10.dp)) {
            Icon(painterResource(id = R.drawable.ic_settings), "")
        }
    }
}


@OptIn(ExperimentalCoilApi::class)
@Composable
fun InfoProfile(
    urlImgProfile: String,
    nameProfile: String,
) {

    val painter = rememberImagePainter(data = urlImgProfile) {
        transformations(CircleCropTransformation())
        placeholder(R.drawable.ic_person)
    }
    val state = painter.state

    Card {
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 30.dp, vertical = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {

            Card(shape = CircleShape) {
                Box(contentAlignment = Alignment.Center) {
                    Image(painter = when (state) {
                        is ImagePainter.State.Error -> painterResource(id = R.drawable.ic_broken_image)
                        else -> painter
                    }, contentDescription = "", modifier = Modifier
                        .size(150.dp)
                        .padding(if (state is ImagePainter.State.Loading) 30.dp else 0.dp)
                    )
                    if (state is ImagePainter.State.Loading) CircularProgressIndicator()
                }
            }


            Spacer(modifier = Modifier.height(20.dp))

            Text(text = nameProfile,
                style = MaterialTheme.typography.body1,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.W600)
        }
    }

}


@Composable
fun StatisticText(
    name: String,
    value: String,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.body1, fontWeight = FontWeight.W600)
        Text(name, style = MaterialTheme.typography.body2)
    }
}