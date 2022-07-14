package com.nullpointer.blogcompose.ui.screens.details

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
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
    val postState by postDetailsViewModel.postState.collectAsState()

    LaunchedEffect(Unit) {
        postDetailsViewModel.initIdPost(idPost)
    }

    LaunchedEffect(key1 = Unit) {
        postDetailsViewModel.messageDetails.collect(postDetailsState::showSnackMessage)
    }

    LaunchedEffect(key1 = Unit) {
        likeViewModel.messageLike.collect(postDetailsState::showSnackMessage)
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
            Resource.Failure -> {
                EmptyScreen(
                    resourceRaw = R.raw.error1, emptyText = stringResource(
                        id = R.string.error_load_post
                    )
                )
            }
            Resource.Loading -> LoadingPost()
            is Resource.Success -> {
                val commentsState by postDetailsViewModel.listComments.collectAsState()
                when (val commentsState = commentsState) {
                    Resource.Failure -> {
                        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                            HeaderBlogDetails(blog = statePost.data)
                            EmptyScreen(
                                resourceRaw = R.raw.error1, emptyText = stringResource(
                                    id = R.string.error_load_comments
                                )
                            )
                        }
                    }
                    Resource.Loading -> {
                        LoadingPost {
                            HeaderBlogDetails(blog = statePost.data)
                        }
                    }
                    is Resource.Success -> CompleteBlogData(listComment = commentsState.data) {
                        HeaderBlogDetails(blog = statePost.data)
                    }
                }
            }
        }
    }
}

@Composable
fun CompleteBlogData(
    listComment: List<Comment>,
    header: @Composable () -> Unit,
) {
    LazyColumn {
        item { header() }
        items(listComment.size) { index ->
            ItemComment(comment = listComment[index])
        }
    }
}

@Composable
private fun HeaderBlogDetails(blog: Post) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(10.dp)
        ) {
            AsyncImage(
                model = ImageRequest
                    .Builder(LocalContext.current)
                    .data(blog.userPoster?.urlImg)
                    .transformations(CircleCropTransformation())
                    .crossfade(true)
                    .build(),
                contentDescription = "",
                placeholder = painterResource(id = R.drawable.ic_person),
                error = painterResource(id = R.drawable.ic_person),
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = blog.userPoster?.name ?: "")
        }
        Text(text = blog.description, modifier = Modifier.padding(10.dp))
        Spacer(modifier = Modifier.height(10.dp))
        AsyncImage(
            model = blog.urlImage,
            contentDescription = "",
            modifier = Modifier
                .fillMaxWidth()
        )
    }
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




