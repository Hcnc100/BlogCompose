package com.nullpointer.blogcompose.ui.screens.homeScreen.profileScreen

import androidx.compose.foundation.layout.*
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
import com.nullpointer.blogcompose.R
import com.nullpointer.blogcompose.core.states.Resource
import com.nullpointer.blogcompose.presentation.AuthViewModel
import com.nullpointer.blogcompose.presentation.LikeViewModel
import com.nullpointer.blogcompose.presentation.MyPostViewModel
import com.nullpointer.blogcompose.ui.screens.destinations.ConfigScreenDestination
import com.nullpointer.blogcompose.ui.screens.destinations.PostDetailsDestination
import com.nullpointer.blogcompose.ui.screens.swipePosts.ScreenSwiperPost
import com.nullpointer.blogcompose.ui.share.ImageProfile
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.flow.collect

@Composable
@Destination(navGraph = "homeDestinations")
fun ProfileScreen(
    authViewModel: AuthViewModel,
    myPostViewModel: MyPostViewModel = hiltViewModel(),
    likeViewModel: LikeViewModel = hiltViewModel(),
    navigator: DestinationsNavigator,
) {

    val stateListPost = myPostViewModel.listMyPost.collectAsState()
    val stateLoading = myPostViewModel.stateLoad.collectAsState()
    val stateConcatenate = myPostViewModel.stateConcatenate.collectAsState()
    val currentUser = authViewModel.currentUser.collectAsState()

    val likeMessage = likeViewModel.messageLike
    val postMessage = myPostViewModel.messageMyPosts

    val scaffoldState = rememberScaffoldState()

    val photoUser = currentUser.value?.urlImg ?: ""
    val name = currentUser.value?.nameUser ?: ""

    LaunchedEffect(postMessage) {
        postMessage.collect {
            scaffoldState.snackbarHostState.showSnackbar(it)
        }
    }

    LaunchedEffect(likeMessage){
        likeMessage.collect {
            scaffoldState.snackbarHostState.showSnackbar(it)
        }
    }

    ScreenSwiperPost(resultListPost = stateListPost.value,
        scaffoldState = scaffoldState,
        updateListPost = { myPostViewModel.requestNewPost(true) },
        actionBottomReached = myPostViewModel::concatenatePost,
        actionChangePost = likeViewModel::likePost,
        staticInfo = Pair(photoUser, name),
        isLoadNewData = stateLoading.value is Resource.Loading,
        isConcatenateData = stateConcatenate.value is Resource.Loading,
        actionDetails = { navigator.navigate(PostDetailsDestination(it)) }
    ) {
        HeaderProfile(
            urlImgProfile = photoUser,
            nameProfile = name,
            actionLogOut = { navigator.navigate(ConfigScreenDestination) }
        )
    }
}

@Composable
fun HeaderProfile(
    urlImgProfile: String,
    nameProfile: String,
    actionLogOut: () -> Unit,
) {
    Box {
        InfoProfile(urlImgProfile, nameProfile)
        IconButton(onClick = { actionLogOut() }, modifier = Modifier
            .align(Alignment.TopEnd)
            .padding(10.dp)) {
            Icon(painterResource(id = R.drawable.ic_settings), "")
        }
    }
}

@Composable
fun InfoProfile(
    urlImgProfile: String,
    nameProfile: String,
) {
    Card {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(30.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {

            ImageProfile(urlImgProfile = urlImgProfile,
                paddingLoading = 30.dp,
                sizeImage = 120.dp)

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