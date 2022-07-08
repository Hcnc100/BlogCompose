package com.nullpointer.blogcompose.ui.screens.homeScreen.blogScreen

import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.nullpointer.blogcompose.R
import com.nullpointer.blogcompose.core.states.Resource
import com.nullpointer.blogcompose.presentation.LikeViewModel
import com.nullpointer.blogcompose.presentation.PostViewModel
import com.nullpointer.blogcompose.ui.navigation.HomeNavGraph
import com.nullpointer.blogcompose.ui.navigation.MainTransitions
import com.nullpointer.blogcompose.ui.screens.swipePosts.ScreenSwiperPost
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@HomeNavGraph(start = true)
@Destination(style = MainTransitions::class)
@Composable
fun BlogScreen(
    postVM: PostViewModel = hiltViewModel(),
    likeVM: LikeViewModel = hiltViewModel(),
    navigator: DestinationsNavigator,
) {
    val resultGetPost = postVM.listPost.collectAsState()
    val stateLoading = postVM.stateLoad.collectAsState()
    val stateConcatenate = postVM.stateConcatenate.collectAsState()
    val scaffoldState = rememberScaffoldState()

    val postMessage = postVM.messagePost
    val likeMessage = likeVM.messageLike
    val context=LocalContext.current

    LaunchedEffect(postMessage) {
        postMessage.collect {
            scaffoldState.snackbarHostState.showSnackbar(
                context.getString(it)
            )
        }
    }

    LaunchedEffect(likeMessage) {
        likeMessage.collect {
            scaffoldState.snackbarHostState.showSnackbar(
                context.getString(it)
            )
        }
    }

    ScreenSwiperPost(resultListPost = resultGetPost.value,
        scaffoldState = scaffoldState,
        updateListPost = { postVM.requestNewPost(true) },
        actionBottomReached = postVM::concatenatePost,
        actionButtonAdd = {
//            navigator.navigate(AddBlogScreenDestination)
                          },
        actionChangePost = likeVM::likePost,
        isLoadNewData = stateLoading.value is Resource.Loading,
        isConcatenateData = stateConcatenate.value is Resource.Loading,
        actionDetails = { idPost, goToBottom ->
//            navigator.navigate(PostDetailsDestination(idPost, goToBottom))
        },
        emptyResRaw = R.raw.empty2,
        emptyString = stringResource(R.string.message_empty_post)
    )
}

