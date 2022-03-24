package com.nullpointer.blogcompose.ui.screens.details

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberImagePainter
import com.google.accompanist.insets.navigationBarsWithImePadding
import com.nullpointer.blogcompose.R
import com.nullpointer.blogcompose.core.states.Resource
import com.nullpointer.blogcompose.models.Comment
import com.nullpointer.blogcompose.models.Post
import com.nullpointer.blogcompose.presentation.LikeViewModel
import com.nullpointer.blogcompose.ui.customs.ToolbarBack
import com.nullpointer.blogcompose.ui.screens.details.componets.Comments
import com.nullpointer.blogcompose.ui.screens.details.viewModel.PostDetailsViewModel
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
    val postState = postDetailsViewModel.postState.collectAsState()
    val commetsState = postDetailsViewModel.commentState.collectAsState()
    val hasNewComments = postDetailsViewModel.hasNewComments.collectAsState()
    val stateRequestComments = postDetailsViewModel.stateConcatenate.collectAsState()
    val detailsMessage = postDetailsViewModel.messageDetails
    val likeMessage = likeViewModel.messageLike
    val scaffoldState = rememberScaffoldState()

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
        list = commetsState.value,
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
    Column {
        val painter = rememberImagePainter(post.urlImage)
        Image(painter = painter,
            contentDescription = "",
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp))
        InfoPost(post = post, actionLike)
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun InfoPost(post: Post, actionLike: (Boolean) -> Unit) {

    var likeState by remember { mutableStateOf(post.ownerLike) }
    var numberLike by remember { mutableStateOf(post.numberLikes) }

    Row(horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)) {

        AnimatedContent(targetState = likeState) {
            Row(modifier = Modifier.clickable {
                actionLike(!likeState)
                likeState = !likeState
                if (likeState) numberLike += 1 else numberLike -= 1
            }) {
                Icon(painterResource(
                    id = if (likeState) R.drawable.ic_fav else R.drawable.ic_unfav),
                    contentDescription = "")
                Spacer(modifier = Modifier.width(10.dp))
                Text("${numberLike} likes")
            }

        }

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

    LaunchedEffect(list) {
        delay(500)
        if (goToBottom) focusRequester.requestFocus()
    }


    Scaffold(
        scaffoldState = scaffoldState,
        topBar = { ToolbarBack(title = "Post", actionBack = actionBack) },
        bottomBar = {
            TextInputComment(
                focusRequester = focusRequester
            ) {
                addComment(it)
                scope.launch {
                    stateLazy.animateScrollToItem(0)
                }
            }
        },
    ) {

        Box(modifier = Modifier
            .fillMaxSize()
            .padding(it)) {
            LazyColumn(
                state = stateLazy,
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    when (post) {
                        is Resource.Failure -> {}
                        is Resource.Loading -> Box(modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                        is Resource.Success -> HeaderBlog(post.data, actionLike = actionLike)
                    }

                }

                when (list) {
                    is Resource.Failure -> {}
                    is Resource.Loading -> {
                        items(10) {
                            Comments()
                        }
                    }
                    is Resource.Success -> {
                        val listComments = list.data
                        items(listComments.size) { index ->
                            Comments(comment = list.data[index])
                        }
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
                                        .clickable { concatenate() }
                                        .padding(vertical = 10.dp)) {
                                        Text(text = "Cargar mas comentarios")
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (hasNewComment)
                Box(modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 35.dp)
                ) {
                    Text(text = "Hay nuevos comentarios", modifier = Modifier
                        .background(MaterialTheme.colors.primary)
                        .padding(horizontal = 10.dp, vertical = 10.dp)
                        .clickable {
                            reloadNewComment()
                        })
                }
        }


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
