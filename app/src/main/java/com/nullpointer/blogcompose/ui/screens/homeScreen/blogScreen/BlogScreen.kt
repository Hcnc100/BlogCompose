package com.nullpointer.blogcompose.ui.screens.homeScreen.blogScreen

import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import com.nullpointer.blogcompose.presentation.PostViewModel
import com.nullpointer.blogcompose.services.UploadPostServices
import com.nullpointer.blogcompose.ui.screens.swipePosts.ScreenSwiperPost
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@Composable
fun BlogScreen(
    postVM: PostViewModel = hiltViewModel(),
    actionGoToAddPost: () -> Unit,
) {
    if (UploadPostServices.updatePostComplete.value) postVM.fetchLastPost()
    val resultGetPost = postVM.listPost.collectAsState()
    val postMessage = postVM.messagePost
    val scaffoldState = rememberScaffoldState()
    LaunchedEffect(postMessage) {
        postMessage.collect {
            scaffoldState.snackbarHostState.showSnackbar(it)
        }
    }

    ScreenSwiperPost(resultListPost = resultGetPost.value,
        scaffoldState = scaffoldState,
        updateListPost = { postVM.fetchLastPost() },
        actionBottomReached = { postVM.concatenateLastPost() },
        actionButtonAdd = actionGoToAddPost,
        actionChangePost = postVM::likePost
    )
}

