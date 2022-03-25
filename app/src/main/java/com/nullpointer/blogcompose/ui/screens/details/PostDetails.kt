package com.nullpointer.blogcompose.ui.screens.details

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberImagePainter
import com.nullpointer.blogcompose.R
import com.nullpointer.blogcompose.core.states.Resource
import com.nullpointer.blogcompose.models.Comment
import com.nullpointer.blogcompose.models.Post
import com.nullpointer.blogcompose.presentation.LikeViewModel
import com.nullpointer.blogcompose.ui.customs.ToolbarBack
import com.nullpointer.blogcompose.ui.screens.dataUser.ImageCirculateUser
import com.nullpointer.blogcompose.ui.screens.details.componets.Comments
import com.nullpointer.blogcompose.ui.screens.details.viewModel.PostDetailsViewModel
import com.nullpointer.blogcompose.ui.share.ImageProfile
import com.ramcosta.composedestinations.annotation.DeepLink
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
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
fun HeaderBlog(post: Post, actionLike: (Boolean) -> Unit) {
    // * image post and number like and comments
    val painter = rememberImagePainter(post.urlImage)
    Column(modifier = Modifier.padding(10.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            ImageProfile(urlImgProfile = post.poster?.urlImg.toString(),
                paddingLoading = 5.dp,
                sizeImage = 30.dp)
            Spacer(modifier = Modifier.width(15.dp))
            Text(post.poster?.name.toString(),
                style = MaterialTheme.typography.body1,
                fontWeight = FontWeight.W600)
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(text = post.description, modifier = Modifier.padding(10.dp))
        Image(painter = painter,
            contentDescription = "",
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp))
        InfoPost(post = post, actionLike)
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun InfoPost(post: Post, actionLike: (Boolean) -> Unit) {

    // * when is in realtime so need mutable state
    var likeState by remember { mutableStateOf(post.ownerLike) }
    var numberLike by remember { mutableStateOf(post.numberLikes) }

    Row(horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)) {
        // * animate content that can change
        AnimatedContent(targetState = likeState) {
            // * button like and number likes
            Row(modifier = Modifier.clickable {
                actionLike(!likeState)
                likeState = !likeState
                if (likeState) numberLike += 1 else numberLike -= 1
            }) {
                Icon(painterResource(
                    id = if (likeState) R.drawable.ic_fav else R.drawable.ic_unfav),
                    contentDescription = "")
                Spacer(modifier = Modifier.width(10.dp))
                Text("$numberLike likes")
            }
        }
        // * info comments
        Text("${post.numberComments} comentarios")
    }
}


@OptIn(ExperimentalComposeUiApi::class)
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
                    ImagePost(
                        statePost = post,
                        actionLike = actionLike)
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
            ButtonRecentComment(
                hasNewComment = hasNewComment,
                actionReload = reloadNewComment,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 35.dp))

        }
    }
}

@Composable
fun ButtonRecentComment(
    hasNewComment: Boolean,
    actionReload: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // * show button when has new comments
    if (hasNewComment) {
        Box(modifier = modifier
        ) {
            Text(text = "Hay nuevos comentarios", modifier = Modifier
                .background(MaterialTheme.colors.primary)
                .padding(horizontal = 10.dp, vertical = 10.dp)
                .clickable { actionReload() })
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
        is Resource.Failure -> {}
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

@Composable
fun ImagePost(
    statePost: Resource<Post>,
    actionLike: (Boolean) -> Unit,
) {
    when (statePost) {
        is Resource.Failure -> {}
        is Resource.Loading -> Box(modifier = Modifier
            .fillMaxWidth()
            .height(250.dp), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        is Resource.Success -> HeaderBlog(statePost.data, actionLike = actionLike)
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TextInputComment(focusRequester: FocusRequester, actionSendComment: (String) -> Unit) {
    val (text, changeText) = rememberSaveable { mutableStateOf("") }
    Box {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
                .height(60.dp)
                .focusRequester(focusRequester),
            value = text,
            onValueChange = changeText,
            singleLine = true,
            label = { Text("Comentario") },
            shape = RoundedCornerShape(20.dp),
            placeholder = { Text("Escribe algo ...") },
            trailingIcon = {
                IconButton(onClick = {
                    if (text.isNotEmpty()) {
                        actionSendComment(text)
                        changeText("")
                    }
                }) {
                    Icon(painterResource(id = R.drawable.ic_send),
                        contentDescription = "",
                        tint = if (text.isEmpty()) Color.Gray else MaterialTheme.colors.primary)
                }
            }
        )
    }
}
