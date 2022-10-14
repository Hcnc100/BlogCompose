package com.nullpointer.blogcompose.ui.screens.details

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
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
import com.nullpointer.blogcompose.ui.navigation.MainNavGraph
import com.nullpointer.blogcompose.ui.screens.details.ActionDetails.*
import com.nullpointer.blogcompose.ui.screens.details.componets.items.post.ErrorDetailsPost
import com.nullpointer.blogcompose.ui.screens.details.componets.items.post.LoadDetailsPost
import com.nullpointer.blogcompose.ui.screens.details.componets.items.post.SuccessDetailsPost
import com.nullpointer.blogcompose.ui.screens.details.componets.lists.ErrorLoadComments
import com.nullpointer.blogcompose.ui.screens.details.componets.lists.LoadListComments
import com.nullpointer.blogcompose.ui.screens.details.componets.lists.SuccessListComments
import com.nullpointer.blogcompose.ui.screens.details.componets.others.TextInputComment
import com.nullpointer.blogcompose.ui.screens.profileScreen.components.FailedProfilePost
import com.nullpointer.blogcompose.ui.screens.states.PostDetailsState
import com.nullpointer.blogcompose.ui.screens.states.rememberPostDetailsState
import com.nullpointer.blogcompose.ui.share.CustomSnackBar
import com.nullpointer.blogcompose.ui.share.ToolbarBack
import com.ramcosta.composedestinations.annotation.DeepLink
import com.ramcosta.composedestinations.annotation.Destination
import com.valentinilk.shimmer.Shimmer
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.merge

@MainNavGraph
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
    actionRootDestinations: ActionRootDestinations,
    likeViewModel: LikeViewModel = hiltViewModel(),
    postDetailsViewModel: PostDetailsViewModel = hiltViewModel(),
    postDetailsState: PostDetailsState = rememberPostDetailsState()
) {
    val postState by postDetailsViewModel.postState.collectAsState()
    var isRequestFocus by rememberSaveable { mutableStateOf(goToBottom) }
    val commentsState by postDetailsViewModel.listComments.collectAsState()

    LaunchedEffect(Unit) {
        postDetailsViewModel.initIdPost(idPost)
    }

    LaunchedEffect(key1 = Unit) {
        merge(postDetailsViewModel.messageDetails, likeViewModel.messageLike).collect(
            postDetailsState::showSnackMessage
        )
    }

    LaunchedEffect(key1 = commentsState, key2 = postState) {
        if (commentsState is Resource.Success && isRequestFocus && postState is Resource.Success) {
            delay(200)
            isRequestFocus = false
            postDetailsState.requestFocus()
            postDetailsState.scrollToLastItem()
        }
    }

    PostDetails(
        statePostDetails = postState,
        stateListComments = commentsState,
        commentProperty = postDetailsViewModel.comment,
        scaffoldState = postDetailsState.scaffoldState,
        lazyListState = postDetailsState.lazyListState,
        focusRequester = postDetailsState.focusRequester,
        numberComments = postDetailsViewModel.numberComments,
        hasNewComments = postDetailsViewModel.hasNewComments,
        isConcatenateComment = postDetailsViewModel.isConcatenateComment,
        actionPostDetails = { action ->
            when (action) {
                ACTION_BACK -> actionRootDestinations.backDestination()
                RELOAD_COMMENTS -> postDetailsViewModel.requestsComments()
                GET_MORE_COMMENTS -> postDetailsViewModel.concatenateComments()
                SEND_COMMENT -> postDetailsViewModel.addComment(postDetailsState::scrollToLastItem)
                LIKE_THIS_POST -> {
                    postDetailsViewModel.currentPost?.let { post ->
                        likeViewModel.likePost(
                            simplePost = post
                        )
                    }
                }
            }
        }
    )
}



@Composable
fun PostDetails(
    numberComments: Int,
    hasNewComments: Boolean,
    lazyListState: LazyListState,
    scaffoldState: ScaffoldState,
    isConcatenateComment: Boolean,
    focusRequester: FocusRequester,
    statePostDetails: Resource<Post>,
    commentProperty: PropertySavableString,
    actionPostDetails: (ActionDetails) -> Unit,
    stateListComments: Resource<List<Comment>>,
    shimmer: Shimmer = rememberShimmer(shimmerBounds = ShimmerBounds.View)
) {
    Box {
        Scaffold(
            topBar = {
                ToolbarBack(
                    title = stringResource(R.string.title_post),
                    actionBack = { actionPostDetails(ACTION_BACK) }
                )
            },
            bottomBar = {
                if (statePostDetails is Resource.Success && stateListComments is Resource.Success)
                    TextInputComment(
                        valueProperty = commentProperty,
                        modifier = Modifier.focusRequester(focusRequester),
                        actionSend = { actionPostDetails(SEND_COMMENT) }
                    )
            }
        ) {
            PostAndComments(
                shimmer = shimmer,
                listState = lazyListState,
                numberComments = numberComments,
                hasNewComments = hasNewComments,
                statePostDetails = statePostDetails,
                actionPostDetails = actionPostDetails,
                stateListComments = stateListComments,
                isConcatenateComment = isConcatenateComment,
                modifier = Modifier
                    .padding(it)
                    .fillMaxSize(),
            ) {
                when (statePostDetails) {
                    Resource.Failure -> FailedProfilePost()
                    Resource.Loading -> LoadDetailsPost(shimmer = shimmer)
                    is Resource.Success -> SuccessDetailsPost(
                        blog = statePostDetails.data,
                        actionLike = { actionPostDetails(LIKE_THIS_POST) })
                }
            }
        }

        CustomSnackBar(
            hostState = scaffoldState.snackbarHostState,
            modifier = Modifier
                .padding(vertical = 80.dp)
                .align(Alignment.TopCenter)
        )
    }
}

@Composable
fun PostAndComments(
    shimmer: Shimmer,
    numberComments: Int,
    hasNewComments: Boolean,
    listState: LazyListState,
    isConcatenateComment: Boolean,
    modifier: Modifier = Modifier,
    sizeBetweenItems: Dp = 20.dp,
    statePostDetails: Resource<Post>,
    actionPostDetails: (ActionDetails) -> Unit,
    stateListComments: Resource<List<Comment>>,
    contentPadding: PaddingValues = PaddingValues(4.dp),
    headerPost: @Composable () -> Unit,
) {
    when (statePostDetails) {
        Resource.Failure -> ErrorDetailsPost(modifier = modifier)
        Resource.Loading -> {
            LoadListComments(
                shimmer = shimmer,
                header = headerPost,
                modifier = modifier,
                contentPadding = contentPadding,
                sizeBetweenItems = sizeBetweenItems
            )
        }
        is Resource.Success -> ListComments(
            shimmer = shimmer,
            modifier = modifier,
            headerPost = headerPost,
            lazyListState = listState,
            contentPadding = contentPadding,
            numberComments = numberComments,
            hasNewComments = hasNewComments,
            sizeBetweenItems = sizeBetweenItems,
            stateListComments = stateListComments,
            isConcatenateComment = isConcatenateComment,
            actionReloadComments = { actionPostDetails(RELOAD_COMMENTS) },
            actionConcatenateComment = { actionPostDetails(GET_MORE_COMMENTS) }
        )
    }
}


@Composable
fun ListComments(
    shimmer: Shimmer,
    numberComments: Int,
    sizeBetweenItems: Dp,
    hasNewComments: Boolean,
    lazyListState: LazyListState,
    isConcatenateComment: Boolean,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
    actionReloadComments: () -> Unit,
    actionConcatenateComment: () -> Unit,
    stateListComments: Resource<List<Comment>>,
    headerPost: @Composable () -> Unit
) {
    when (stateListComments) {
        Resource.Failure -> ErrorLoadComments(
            modifier = modifier,
            header = headerPost,
            sizeBetweenItems = sizeBetweenItems
        )
        Resource.Loading -> LoadListComments(
            shimmer = shimmer,
            modifier = modifier,
            header = headerPost,
            contentPadding = contentPadding,
            sizeBetweenItems = sizeBetweenItems,
        )
        is Resource.Success -> SuccessListComments(
            modifier = modifier,
            header = headerPost,
            lisState = lazyListState,
            numberComments = numberComments,
            hasNewComments = hasNewComments,
            contentPadding = contentPadding,
            sizeBetweenItems = sizeBetweenItems,
            listComments = stateListComments.data,
            actionReloadComments = actionReloadComments,
            isConcatenateComments = isConcatenateComment,
            actionConcatenateComments = actionConcatenateComment
        )
    }
}


