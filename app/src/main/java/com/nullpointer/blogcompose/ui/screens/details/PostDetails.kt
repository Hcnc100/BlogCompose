package com.nullpointer.blogcompose.ui.screens.details

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nullpointer.blogcompose.core.states.Resource
import com.nullpointer.blogcompose.models.Comment
import com.nullpointer.blogcompose.models.posts.Post
import com.nullpointer.blogcompose.presentation.LikeViewModel
import com.nullpointer.blogcompose.ui.customs.ToolbarBack
import com.nullpointer.blogcompose.ui.screens.details.componets.ButtonHasNewComment
import com.nullpointer.blogcompose.ui.screens.details.componets.Comments
import com.nullpointer.blogcompose.ui.screens.details.componets.DataPost
import com.nullpointer.blogcompose.ui.screens.details.componets.TextInputComment
import com.nullpointer.blogcompose.ui.screens.details.viewModel.PostDetailsViewModel
import com.ramcosta.composedestinations.annotation.DeepLink
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Destination(
    deepLinks = [
        DeepLink(
            uriPattern = "https://www.blog-compose.com/post/{idPost}"
        )
    ]
)
@Composable
fun PostDetails(
    idPost: String,
    goToBottom: Boolean = false,
    navigator: DestinationsNavigator,
    postDetailsViewModel: PostDetailsViewModel = hiltViewModel(),
    likeViewModel: LikeViewModel = hiltViewModel(),
) {
    // * state
    val postState = postDetailsViewModel.postState.collectAsState()
    val commentsState = postDetailsViewModel.commentState.collectAsState()
    val hasNewComments = postDetailsViewModel.hasNewComments.collectAsState()
    val stateRequestComments = postDetailsViewModel.stateConcatenate.collectAsState()
    // * messages post
    val detailsMessage = postDetailsViewModel.messageDetails
    val likeMessage = likeViewModel.messageLike
    val scaffoldState = rememberScaffoldState()

    // * init post loading (likes and comments)
    LaunchedEffect(Unit) {
        postDetailsViewModel.initIdPost(idPost)
    }

    LaunchedEffect(detailsMessage) {
        detailsMessage.collect {
            scaffoldState.snackbarHostState.showSnackbar(it)
        }
    }

    LaunchedEffect(likeMessage) {
        likeMessage.collect {
            scaffoldState.snackbarHostState.showSnackbar(it)
        }
    }

    PostReal(post = postState.value,
        list = commentsState.value,
        scaffoldState = scaffoldState,
        hasNewComment = hasNewComments.value,
        goToBottom = goToBottom,
        stateRequestComments = stateRequestComments.value,
        concatenate = postDetailsViewModel::concatenateComments,
        totalComments = postDetailsViewModel.numberComments,
        reloadNewComment = postDetailsViewModel::reloadNewComment,
        actionBack = navigator::popBackStack,
        actionLike = { likeViewModel.likePost(idPost, it) },
        addComment = { postDetailsViewModel.addComment(idPost, it) }
    )

}

@Composable
fun PostReal(
    post: Resource<Post>,
    list: Resource<List<Comment>>,
    scaffoldState: ScaffoldState,
    hasNewComment: Boolean,
    totalComments: Int,
    goToBottom: Boolean,
    stateRequestComments: Resource<Unit>?,
    actionLike: (Boolean) -> Unit,
    reloadNewComment: () -> Unit,
    concatenate: () -> Unit,
    actionBack: () -> Unit,
    addComment: (String) -> Unit,
) {

    val stateLazy = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }

    // ! this for no overlap text input
    LaunchedEffect(list) {
        delay(500)
        if (goToBottom) focusRequester.requestFocus()
    }


    Scaffold(
        scaffoldState = scaffoldState,
        topBar = { ToolbarBack(title = "Post", actionBack = actionBack) },
        bottomBar = {
            // * input comment
            TextInputComment(
                // * request focus when click in icon comment
                // * when enter normal no request focus
                focusRequester = focusRequester,
                // * when add new comment, so scroll to start list
                // * list order is ascending (more recent first)
                actionSendComment = {
                    addComment(it)
                    scope.launch {
                        stateLazy.animateScrollToItem(0)
                    }
                }
            )
        },
    ) {
        // * needed a box to positioned button "has new comments"
        // * because this is "floating"
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(it)) {
            LazyColumn(
                state = stateLazy,
                modifier = Modifier.fillMaxWidth()
            ) {
                // * image with post
                item {
                    DataPost(
                        statePost = post,
                        actionLike = actionLike,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                    )
                }
                // * comments list
                listComments(listState = list,
                    stateRequestComments = stateRequestComments,
                    totalComments = totalComments,
                    lazyListScope = this,
                    actionConcatenate = concatenate
                )
            }
            // * floating button "has new comments"
            // ? only show when has new comments xd
            ButtonHasNewComment(
                hasNewComment = hasNewComment,
                actionReload = reloadNewComment,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 35.dp))

        }
    }
}


fun listComments(
    listState: Resource<List<Comment>>,
    stateRequestComments: Resource<Unit>?,
    totalComments: Int,
    lazyListScope: LazyListScope,
    actionConcatenate: () -> Unit,
) = with(lazyListScope) {

    when (listState) {
        is Resource.Failure -> Unit
        is Resource.Loading -> {
            // * show empty comments (with shimmer, as facebook)
            items(10) { Comments() }
        }
        is Resource.Success -> {
            val listComments = listState.data
            // * show list comments
            items(listComments.size) { index ->
                Comments(comment = listState.data[index])
            }
            // * if has comments and no load all, so show clicked item
            // * that load more, this change a progress circular when load state
            if (listComments.isNotEmpty() && listComments.size != totalComments) {
                item {
                    if (stateRequestComments is Resource.Loading) {
                        Box(modifier = Modifier.fillMaxWidth()) {
                            CircularProgressIndicator(modifier = Modifier.size(25.dp),
                                strokeWidth = 4.dp)
                        }
                    } else {
                        Box(modifier = Modifier
                            .fillMaxWidth()
                            .clickable { actionConcatenate() }
                            .padding(vertical = 10.dp)) {
                            Text(text = "Cargar mas comentarios")
                        }
                    }
                }
            }
        }
    }
}




