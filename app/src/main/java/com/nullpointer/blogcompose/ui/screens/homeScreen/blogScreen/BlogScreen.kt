package com.nullpointer.blogcompose.ui.screens.homeScreen.blogScreen

import androidx.compose.animation.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.nullpointer.blogcompose.R
import com.nullpointer.blogcompose.core.utils.Resource
import com.nullpointer.blogcompose.models.Post
import com.nullpointer.blogcompose.presentation.PostViewModel
import com.nullpointer.blogcompose.ui.screens.homeScreen.blogScreen.componets.BlogItem
import timber.log.Timber

@Composable
fun BlogScreen(
    postVM: PostViewModel = hiltViewModel(),
) {
    val stateList= rememberLazyListState()
    Scaffold(
        floatingActionButton = {
           ButtonAdd(isScrollInProgress = stateList.isScrollInProgress)
        }
    ) {
        val listPostState = postVM.listPost.collectAsState()

        when (val state = listPostState.value) {
            is Resource.Failure -> {
                Timber.d(state.exception)
            }
            is Resource.Loading -> Box(modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            is Resource.Success -> {
                val listPost = state.data
                LazyColumn(state = stateList) {
                    items(listPost.size) { index ->
                        BlogItem(listPost[index])
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ButtonAdd(
    isScrollInProgress: Boolean,
    action: () -> Unit = {},
) {
    AnimatedVisibility(
        visible = !isScrollInProgress,
        enter = scaleIn() + fadeIn(),
        exit = scaleOut() + fadeOut()
    ) {
        FloatingActionButton(onClick = { /*TODO*/ }){
            Icon(painterResource(id = R.drawable.ic_add),
                contentDescription = "")
        }
    }
}
