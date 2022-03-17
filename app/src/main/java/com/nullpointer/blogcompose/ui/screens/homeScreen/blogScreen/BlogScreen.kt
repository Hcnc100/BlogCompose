package com.nullpointer.blogcompose.ui.screens.homeScreen.blogScreen

import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import com.nullpointer.blogcompose.core.states.Resource
import com.nullpointer.blogcompose.presentation.PostViewModel
import com.nullpointer.blogcompose.ui.screens.destinations.AddBlogScreenDestination
import com.nullpointer.blogcompose.ui.screens.swipePosts.ScreenSwiperPost
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.flow.collect

@Destination(
    navGraph = "homeDestinations", start = true)
@Composable
fun BlogScreen(
    postVM: PostViewModel = hiltViewModel(),
    navigator: DestinationsNavigator,
) {
    val resultGetPost = postVM.listPost.collectAsState()
    val stateLoading = postVM.stateLoad.collectAsState()
    val stateConcatenate = postVM.stateConcatenate.collectAsState()
    val postMessage = postVM.messagePost
    val scaffoldState = rememberScaffoldState()
    LaunchedEffect(postMessage) {
        postMessage.collect {
            scaffoldState.snackbarHostState.showSnackbar(it)
        }
    }

    ScreenSwiperPost(resultListPost = resultGetPost.value,
        scaffoldState = scaffoldState,
        updateListPost = { postVM.requestNewPost(true) },
        actionBottomReached = postVM::concatenatePost,
        actionButtonAdd = { navigator.navigate(AddBlogScreenDestination) },
        actionChangePost = postVM::likePost,
        isLoadNewData = stateLoading.value is Resource.Loading,
        isConcatenateData = stateConcatenate.value is Resource.Loading
    )
}

