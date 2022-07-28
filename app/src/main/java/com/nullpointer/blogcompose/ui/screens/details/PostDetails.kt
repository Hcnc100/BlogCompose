package com.nullpointer.blogcompose.ui.screens.details

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.nullpointer.blogcompose.models.posts.Post
import com.nullpointer.blogcompose.presentation.LikeViewModel
import com.nullpointer.blogcompose.presentation.PostDetailsViewModel
import com.nullpointer.blogcompose.ui.interfaces.ActionRootDestinations
import com.nullpointer.blogcompose.ui.navigation.RootNavGraph
import com.nullpointer.blogcompose.ui.screens.details.componets.*
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
    val commentsState by postDetailsViewModel.listComments.collectAsState()
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

    LaunchedEffect(key1 = commentsState, key2 = postState) {
        if (commentsState is Resource.Success && isRequestFocus && postState is Resource.Success) {
            delay(200)
            isRequestFocus = false
            focusRequester.requestFocus()
            postDetailsState.lazyListState.animateScrollToItem(postDetailsState.lazyListState.layoutInfo.totalItemsCount)
        }
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

        PostDetails(
            modifier = Modifier.padding(it),
            listComments = commentsState,
            postState = postState,
            isConcatenate = postDetailsViewModel.stateConcatComment,
            listState = postDetailsState.lazyListState,
            hasNewComments = postDetailsViewModel.hasNewComments,
            addingComment = postDetailsViewModel.addingComment,
            realNumberComment = postDetailsViewModel.numberComments,
            actionDetails = { action ->
                when (action) {
                    ActionDetails.RELOAD_COMMENTS -> postDetailsViewModel.requestsComments()
                    ActionDetails.LIKE_THIS_POST -> postDetailsViewModel.currentPost?.let { post ->
                        likeViewModel.likePost(
                            simplePost = post
                        )
                    }
                    ActionDetails.GET_MORE_COMMENTS -> postDetailsViewModel.concatenateComments()
                }
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PostDetails(
    postState: Resource<Post>,
    listComments: Resource<List<Comment>>,
    modifier: Modifier = Modifier,
    isConcatenate: Boolean,
    listState: LazyListState,
    hasNewComments: Boolean,
    addingComment: Boolean,
    realNumberComment: Int,
    actionDetails: (ActionDetails) -> Unit,
) {

    Box(modifier = modifier) {
        LazyColumn(state = listState, modifier = Modifier.fillMaxWidth()) {
            when (postState) {
                Resource.Loading -> item(key =  "fake-details" ) { FakeHeaderBlogDetails() }
                Resource.Failure -> {
                    item(key =  "failed-details" ) {
                        AnimationScreen(
                            resourceRaw = R.raw.error1,
                            emptyText = stringResource(id = R.string.error_load_post),
                            modifier = Modifier.fillParentMaxHeight(1f)
                        )
                    }
                }
                is Resource.Success -> {
                    item(key =  postState.data.id ) {
                        HeaderBlogDetails(
                            blog = postState.data,
                            actionLike = { actionDetails(ActionDetails.LIKE_THIS_POST) })
                    }
                    when (listComments) {
                        Resource.Failure -> {
                            item(key =  "fail-comments" ) {
                                ErrorLoadingComments(
                                    actionReloadComments = { actionDetails(ActionDetails.RELOAD_COMMENTS) },
                                )
                            }
                        }
                        Resource.Loading -> {
                            items(
                                count = 10,
                                key = { "comment-loading $it" }
                            ) {
                                FakeItemBlog()
                            }
                        }
                        is Resource.Success -> {
                            item(key =  "has-more-comments" ) {
                                when {
                                    isConcatenate-> CircularProgressIndicator(
                                        modifier = Modifier
                                            .padding(10.dp)
                                            .size(25.dp)
                                    )
                                    !addingComment && realNumberComment > listComments.data.size -> Text(
                                        stringResource(id = R.string.text_load_more_comments),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { actionDetails(ActionDetails.GET_MORE_COMMENTS) }
                                            .padding(10.dp))
                                    else -> Spacer(modifier = Modifier.fillMaxWidth())
                                }
                            }

                            items(listComments.data, key = { it.id }) {
                                ItemComment(
                                    comment = it,
                                    modifier = Modifier.animateItemPlacement()
                                )
                            }
                        }
                    }
                }
            }
        }
        if (hasNewComments)
            TextNewComments(
                modifier = Modifier.align(Alignment.BottomCenter),
                actionReloadNewComments = {
                    actionDetails(ActionDetails.RELOAD_COMMENTS)
                }
            )
    }
}


@Composable
private fun TextNewComments(
    modifier: Modifier = Modifier,
    actionReloadNewComments: () -> Unit
) {
    Text(
        stringResource(id = R.string.message_has_new_comments),
        modifier = modifier
            .padding(10.dp)
            .background(MaterialTheme.colors.primary)
            .padding(10.dp)
            .clip(RoundedCornerShape(10.dp))
            .clickable { actionReloadNewComments() }

    )
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





