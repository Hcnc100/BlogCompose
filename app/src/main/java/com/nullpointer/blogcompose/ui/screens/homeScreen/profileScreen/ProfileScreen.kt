package com.nullpointer.blogcompose.ui.screens.homeScreen.profileScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
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
import com.nullpointer.blogcompose.presentation.AuthViewModel
import com.nullpointer.blogcompose.presentation.PostViewModel
import com.nullpointer.blogcompose.services.uploadImg.UploadPostServices
import com.nullpointer.blogcompose.ui.screens.swipePosts.ScreenSwiperPost

@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel = hiltViewModel(),
    postViewModel: PostViewModel = hiltViewModel(),
) {
    val listMyPost = postViewModel.listMyPost.collectAsState()
    val scaffoldState = rememberScaffoldState()
    val snackbarHostState=scaffoldState.snackbarHostState

    ScreenSwiperPost(resultListPost = listMyPost.value,
        scaffoldState = scaffoldState,
        updateListPost = { postViewModel.fetchMyLastPost() },
        actionBottomReached = {  },
        actionChangePost = postViewModel::likePost,
        snackbarHostState = snackbarHostState
    ) {
        HeaderProfile(urlImgProfile = authViewModel.photoUser)
    }
}

@Composable
fun HeaderProfile(
    urlImgProfile: String,
) {
    Box {
        InfoProfile(urlImgProfile)
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

            Text(text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vestibulum quis ligula tincidunt, imperdiet odio id, accumsan nibh. Ut mollis magna vitae condimentum aliquam.",
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