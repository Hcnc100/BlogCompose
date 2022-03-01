package com.nullpointer.blogcompose.ui.screens.homeScreen.blogScreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import com.nullpointer.blogcompose.presentation.PostViewModel
import com.nullpointer.blogcompose.services.UploadPostServices
import com.nullpointer.blogcompose.ui.screens.swipePosts.ScreenSwiperPost

@Composable
fun BlogScreen(
    postVM: PostViewModel = hiltViewModel(),
    actionGoToAddPost: () -> Unit,
) {
    if (UploadPostServices.updatePostComplete.value) postVM.fetchLastPost()
    val resultGetPost = postVM.listPost.collectAsState()

    ScreenSwiperPost(resultListPost = resultGetPost.value,
        updateListPost = { postVM.fetchLastPost() },
        actionBottomReached = { postVM.concatenateLastPost() },
        actionButtonAdd = actionGoToAddPost
    )
}

