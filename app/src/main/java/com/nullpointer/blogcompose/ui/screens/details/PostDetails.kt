package com.nullpointer.blogcompose.ui.screens.details

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
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
    navigator: DestinationsNavigator,
    idPost: String,
    postDetailsViewModel: PostDetailsViewModel = hiltViewModel(),
) {

    LaunchedEffect(Unit) {
        postDetailsViewModel.initIdPost(idPost)
    }
    val postState = postDetailsViewModel.postState.collectAsState()
    val commetsState = postDetailsViewModel.commentState.collectAsState()
    PostReal(post = postState.value, list = commetsState.value, navigator::popBackStack) {comment,callback->
        postDetailsViewModel.addComment(idPost, comment,callback)
    }
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
    actionBack: () -> Unit,
    addComment: (String,callBack:()->Unit) -> Unit,
) {

    val stateLazy = rememberLazyListState()
    val scope= rememberCoroutineScope()
    var sizeComment by remember { mutableStateOf(0) }

    Scaffold(
        topBar = { ToolbarBack(title = "Post", actionBack = actionBack) },
        bottomBar = { TextInputComment(stateLazy, sizeComment){
            addComment(it){
                scope.launch {
                    stateLazy.animateScrollToItem(sizeComment)
                }
            }
        } },
    ) {

        LazyColumn(
            Modifier.padding(it),
            state = stateLazy,
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
                is Resource.Loading -> {}
                is Resource.Success -> {
                    items(list.data.size) { index ->
                        sizeComment = list.data.size
                        val comment = list.data[index]
                        Comments(
                            comment.urlImg,
                            comment.nameUser,
                            comment.timestamp?.time ?: 0,
                            comment.comment
                        )
                    }
                }
            }
        }

    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TextInputComment(
    lazyListState: LazyListState,
    lastPosition: Int,
    actionSendComment: (String) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val (text, changeText) = rememberSaveable { mutableStateOf("") }
    Box() {
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
