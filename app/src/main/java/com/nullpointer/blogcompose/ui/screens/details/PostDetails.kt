package com.nullpointer.blogcompose.ui.screens.details

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberImagePainter
import com.nullpointer.blogcompose.R
import com.nullpointer.blogcompose.core.states.Resource
import com.nullpointer.blogcompose.models.Comment
import com.nullpointer.blogcompose.models.Post
import com.nullpointer.blogcompose.ui.customs.ToolbarBack
import com.nullpointer.blogcompose.ui.screens.details.componets.Comments
import com.nullpointer.blogcompose.ui.screens.details.viewModel.PostDetailsViewModel
import com.ramcosta.composedestinations.annotation.DeepLink
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.valentinilk.shimmer.shimmer
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
    navigator: DestinationsNavigator,
    postDetailsViewModel: PostDetailsViewModel = hiltViewModel(),
) {

    LaunchedEffect(Unit) {
        postDetailsViewModel.initIdPost(idPost)
    }

    val postState = postDetailsViewModel.postState.collectAsState()
    val commetsState = postDetailsViewModel.commentState.collectAsState()
    val hasNewComments = postDetailsViewModel.hasNewComments.collectAsState()
    val stateRequestComments = postDetailsViewModel.stateConcatenate.collectAsState()
    val scaffoldState = rememberScaffoldState()
    val detailsMessage = postDetailsViewModel.messageDetails

    LaunchedEffect(detailsMessage) {
        detailsMessage.collect {
            scaffoldState.snackbarHostState.showSnackbar(it)
        }
    }
    PostReal(post = postState.value,
        list = commetsState.value,
        scaffoldState = scaffoldState,
        hasNewComment = hasNewComments.value,
        stateRequestComments = stateRequestComments.value,
        concatenate = postDetailsViewModel::concatenateComments,
        totalComments = postDetailsViewModel.numberComments,
        reloadNewComment = postDetailsViewModel::reloadNewComment,
        actionBack = navigator::popBackStack,
        addComment = { postDetailsViewModel.addComment(idPost, it) }
    )

}


@Composable
fun HeaderBlog(post: Post) {
    Column {
        val painter = rememberImagePainter(post.urlImage)
        Image(painter = painter,
            contentDescription = "",
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp))
        InfoPost(post = post)
    }
}

@Composable
fun InfoPost(post: Post) {
    Row(horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)) {
        Row {
            val res = if (post.ownerLike) R.drawable.ic_fav else R.drawable.ic_unfav
            Image(painter = painterResource(id = res), contentDescription = "")
            Spacer(modifier = Modifier.width(10.dp))
            Text("${post.numberLikes} likes")
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
    stateRequestComments: Resource<Unit>?,
    reloadNewComment: () -> Unit,
    concatenate: () -> Unit,
    actionBack: () -> Unit,
    addComment: (String) -> Unit,
) {

    val stateLazy = rememberLazyListState()
    val scope = rememberCoroutineScope()

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = { ToolbarBack(title = "Post", actionBack = actionBack) },
        bottomBar = {
            TextInputComment {
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
                        is Resource.Success -> HeaderBlog(post.data)
                    }

                }

                when (list) {
                    is Resource.Failure -> {}
                    is Resource.Loading -> {
                        items(10){
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
fun TextInputComment(actionSendComment: (String) -> Unit) {
    val (text, changeText) = rememberSaveable { mutableStateOf("") }
    Box {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
                .height(60.dp),
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
