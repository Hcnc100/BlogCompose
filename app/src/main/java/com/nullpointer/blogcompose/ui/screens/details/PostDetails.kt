package com.nullpointer.blogcompose.ui.screens.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nullpointer.blogcompose.R
import com.nullpointer.blogcompose.core.delegates.PropertySavableString
import com.nullpointer.blogcompose.core.states.Resource
import com.nullpointer.blogcompose.models.Comment
import com.nullpointer.blogcompose.models.posts.ActionDetails
import com.nullpointer.blogcompose.models.posts.Post
import com.nullpointer.blogcompose.presentation.LikeViewModel
import com.nullpointer.blogcompose.presentation.PostDetailsViewModel
import com.nullpointer.blogcompose.ui.interfaces.ActionRootDestinations
import com.nullpointer.blogcompose.ui.navigation.RootNavGraph
import com.nullpointer.blogcompose.ui.screens.details.componets.ErrorLoadingOnlyComments
import com.nullpointer.blogcompose.ui.screens.details.componets.LoadingFullPostDetails
import com.nullpointer.blogcompose.ui.screens.details.componets.LoadingOnlyComments
import com.nullpointer.blogcompose.ui.screens.details.componets.SuccessFullDetails
import com.nullpointer.blogcompose.ui.screens.emptyScreen.AnimationScreen
import com.nullpointer.blogcompose.ui.screens.states.PostDetailsState
import com.nullpointer.blogcompose.ui.screens.states.rememberPostDetailsState
import com.nullpointer.blogcompose.ui.share.EditableTextSavable
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
    postDetailsState: PostDetailsState = rememberPostDetailsState(),
    actionRootDestinations: ActionRootDestinations
) {
    val postState by postDetailsViewModel.postState.collectAsState()
    val focusRequester = remember { FocusRequester() }
    var isRequestFocus by remember {
        mutableStateOf(goToBottom)
    }

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
        },
        bottomBar = {
            if (postState !is Resource.Failure)
                TextInputComment(
                    modifier = Modifier.focusRequester(focusRequester),
                    valueProperty = postDetailsViewModel.comment,
                    actionSend = {
                        postDetailsState.scope.launch {
                            postDetailsViewModel.addComment(it).join()
                            delay(200)
                            postDetailsState.lazyListState.animateScrollToItem(postDetailsState.lazyListState.layoutInfo.totalItemsCount - 1)
                        }
                    }
                )
        }
    ) {
        when (val postState = postState) {
            Resource.Failure -> {
                AnimationScreen(
                    resourceRaw = R.raw.error1,
                    emptyText = stringResource(id = R.string.error_load_post),
                    modifier = Modifier.padding(it).fillMaxSize()
                )
            }
            Resource.Loading -> LoadingFullPostDetails(modifier = Modifier.padding(it))
            is Resource.Success -> {
                val commentsState by postDetailsViewModel.listComments.collectAsState()

                LaunchedEffect(key1 = commentsState) {
                    if (commentsState is Resource.Success && isRequestFocus) {
                        delay(200)
                        isRequestFocus = false
                        focusRequester.requestFocus()
                        postDetailsState.lazyListState.animateScrollToItem(postDetailsState.lazyListState.layoutInfo.totalItemsCount)
                    }
                }


                FullDetailsScreen(
                    modifier = Modifier.padding(it),
                    commentsState = commentsState,
                    post = postState.data,
                    isConcatenate = postDetailsViewModel.stateConcatComment,
                    listState = postDetailsState.lazyListState,
                    hasNewComments = postDetailsViewModel.hasNewComments,
                    actionReloadComments = postDetailsViewModel::requestsComments,
                    actionDetails = { action ->
                        when (action) {
                            ActionDetails.HAS_NEW_COMMENTS -> postDetailsViewModel.requestsComments()
                            ActionDetails.LIKE_THIS_POST -> likeViewModel.likePost(simplePost = postState.data)
                            ActionDetails.GET_MORE_COMMENTS -> postDetailsViewModel.concatenateComments()
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun FullDetailsScreen(
    commentsState: Resource<List<Comment>>,
    post: Post,
    modifier: Modifier = Modifier,
    isConcatenate: Boolean,
    listState: LazyListState,
    hasNewComments: Boolean,
    actionDetails: (ActionDetails) -> Unit,
    actionReloadComments: () -> Unit
) {
    when (commentsState) {
        Resource.Failure -> {
            ErrorLoadingOnlyComments(
                post = post,
                modifier = modifier,
                actionLike = { actionDetails(ActionDetails.LIKE_THIS_POST) },
                actionReloadComments = actionReloadComments
            )
        }
        Resource.Loading -> {
            LoadingOnlyComments(
                post = post,
                modifier = modifier,
                actionLike = { actionDetails(ActionDetails.LIKE_THIS_POST) }
            )
        }
        is Resource.Success -> {
            SuccessFullDetails(
                isLoading = isConcatenate,
                listState = listState,
                listComment = commentsState.data,
                post = post,
                modifier = modifier,
                hasNewComments = hasNewComments,
                actionPost = actionDetails,
            )
        }
    }
}


@Composable
private fun TextInputComment(
    valueProperty: PropertySavableString,
    actionSend: (String) -> Unit,
    modifier: Modifier
) {
    val actionSendValidate = {
        if (valueProperty.hasChanged) {
            actionSend(valueProperty.value)
            valueProperty.clearValue()
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        EditableTextSavable(
            valueProperty = valueProperty,
            shape = RoundedCornerShape(15.dp),
            modifier = modifier.weight(0.8f),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Send
            ),
            keyboardActions = KeyboardActions(
                onSend = { actionSendValidate() }
            )
        )
        IconButton(onClick = actionSendValidate) {
            Icon(
                painter = painterResource(id = R.drawable.ic_send),
                contentDescription = stringResource(id = R.string.description_send_comment),
                tint = if (valueProperty.hasChanged) MaterialTheme.colors.primary else Color.Unspecified
            )
        }
    }
}





