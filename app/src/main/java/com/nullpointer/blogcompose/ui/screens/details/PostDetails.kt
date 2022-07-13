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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nullpointer.blogcompose.R
import com.nullpointer.blogcompose.core.states.Resource
import com.nullpointer.blogcompose.core.utils.SimpleScreenState
import com.nullpointer.blogcompose.core.utils.rememberSimpleScreenState
import com.nullpointer.blogcompose.models.Comment
import com.nullpointer.blogcompose.models.posts.Post
import com.nullpointer.blogcompose.presentation.LikeViewModel
import com.nullpointer.blogcompose.ui.interfaces.ActionRootDestinations
import com.nullpointer.blogcompose.ui.navigation.RootNavGraph
import com.nullpointer.blogcompose.ui.screens.details.componets.ButtonHasNewComment
import com.nullpointer.blogcompose.ui.screens.details.componets.Comments
import com.nullpointer.blogcompose.ui.screens.details.componets.DataPost
import com.nullpointer.blogcompose.ui.screens.details.componets.TextInputComment
import com.nullpointer.blogcompose.ui.screens.details.viewModel.PostDetailsViewModel
import com.nullpointer.blogcompose.ui.screens.emptyScreen.EmptyScreen
import com.nullpointer.blogcompose.ui.share.ToolbarBack
import com.ramcosta.composedestinations.annotation.DeepLink
import com.ramcosta.composedestinations.annotation.Destination
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@RootNavGraph
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
    postDetailsViewModel: PostDetailsViewModel = hiltViewModel(),
    likeViewModel: LikeViewModel = hiltViewModel(),
    postDetailsState: SimpleScreenState = rememberSimpleScreenState(),
    actionRootDestinations: ActionRootDestinations
) {
    // * state
    val postState by postDetailsViewModel.postState.collectAsState()
    val commentsState = postDetailsViewModel.commentState.collectAsState()
    val hasNewComments = postDetailsViewModel.hasNewComments.collectAsState()
    val stateRequestComments = postDetailsViewModel.stateConcatenate.collectAsState()
    // * messages post
    val detailsMessage = postDetailsViewModel.messageDetails
    val likeMessage = likeViewModel.messageLike
    val scaffoldState = rememberScaffoldState()
    val context = LocalContext.current

    // * init post loading (likes and comments)
//    LaunchedEffect(Unit) {
//        postDetailsViewModel.initIdPost(idPost)
//    }

    LaunchedEffect(detailsMessage) {
        detailsMessage.collect(postDetailsState::showSnackMessage)
    }

    LaunchedEffect(likeMessage) {
        likeMessage.collect(postDetailsState::showSnackMessage)
    }

    Scaffold(
        scaffoldState = postDetailsState.scaffoldState,
        topBar = {
            ToolbarBack(
                title = stringResource(R.string.title_post),
                actionBack = actionRootDestinations::backDestination
            )
        }
    ) {
        when (val statePost = postState) {
            Resource.Failure -> EmptyScreen(
                resourceRaw = R.raw.error1, emptyText = stringResource(
                    id = R.string.error_load_post
                )
            )
            Resource.Loading -> LoadingPost()
            is Resource.Success -> {}
        }
    }


//    PostReal(post = postState.value,
//        list = commentsState.value,
//        scaffoldState = scaffoldState,
//        hasNewComment = hasNewComments.value,
//        goToBottom = goToBottom,
//        stateRequestComments = stateRequestComments.value,
//        concatenate = postDetailsViewModel::concatenateComments,
//        totalComments = postDetailsViewModel.numberComments,
//        reloadNewComment = postDetailsViewModel::reloadNewComment,
//        actionBack = navigator::popBackStack,
//        actionLike = {
//            postDetailsViewModel.post?.let { post ->
//                likeViewModel.likePost(
//                    post,
//                    it
//                )
//            }
//        },
//        addComment = { postDetailsViewModel.addComment(it) }
//    )

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
        topBar = {
            ToolbarBack(
                title = stringResource(R.string.title_post),
                actionBack = actionBack
            )
        },
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
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
                listComments(
                    listState = list,
                    stateRequestComments = stateRequestComments,
                    totalComments = totalComments,
                    lazyListScope = this,
                    actionConcatenate = concatenate
                )
            }
            // * floating button "has new comments"
            // ? only show when has new comments xd
            if (hasNewComment)
                ButtonHasNewComment(
                    actionReload = reloadNewComment,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 35.dp)
                )

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
                            CircularProgressIndicator(
                                modifier = Modifier.size(25.dp),
                                strokeWidth = 4.dp
                            )
                        }
                    } else {
                        Box(modifier = Modifier
                            .fillMaxWidth()
                            .clickable { actionConcatenate() }
                            .padding(vertical = 10.dp)) {
                            Text(text = stringResource(R.string.text_load_more_comments))
                        }
                    }
                }
            }
        }
    }
}




