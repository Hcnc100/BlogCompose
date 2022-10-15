package com.nullpointer.blogcompose.ui.screens.blogScreen

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.ScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.nullpointer.blogcompose.R
import com.nullpointer.blogcompose.actions.ActionBlogScreen
import com.nullpointer.blogcompose.actions.ActionBlogScreen.*
import com.nullpointer.blogcompose.actions.ActionsPost
import com.nullpointer.blogcompose.actions.ActionsPost.*
import com.nullpointer.blogcompose.core.states.Resource
import com.nullpointer.blogcompose.models.posts.Post
import com.nullpointer.blogcompose.models.posts.SimplePost
import com.nullpointer.blogcompose.presentation.LikeViewModel
import com.nullpointer.blogcompose.presentation.PostViewModel
import com.nullpointer.blogcompose.services.uploadImg.UploadDataServices
import com.nullpointer.blogcompose.ui.interfaces.ActionRootDestinations
import com.nullpointer.blogcompose.ui.navigation.HomeNavGraph
import com.nullpointer.blogcompose.ui.navigation.MainTransitions
import com.nullpointer.blogcompose.ui.screens.blogScreen.components.list.ListEmptyBlog
import com.nullpointer.blogcompose.ui.screens.blogScreen.components.list.ListLoadBlog
import com.nullpointer.blogcompose.ui.screens.blogScreen.components.list.ListSuccessBlog
import com.nullpointer.blogcompose.ui.screens.destinations.AddBlogScreenDestination
import com.nullpointer.blogcompose.ui.screens.destinations.PostDetailsDestination
import com.nullpointer.blogcompose.ui.screens.states.SwipeRefreshScreenState
import com.nullpointer.blogcompose.ui.screens.states.rememberSwipeRefreshScreenState
import com.nullpointer.blogcompose.ui.share.ButtonAdd
import com.nullpointer.blogcompose.ui.share.CustomSnackBar
import com.nullpointer.blogcompose.ui.share.ScaffoldSwipe
import com.ramcosta.composedestinations.annotation.Destination
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.merge

@HomeNavGraph(start = true)
@Destination(style = MainTransitions::class)
@Composable
fun BlogScreen(
    postVM: PostViewModel = hiltViewModel(),
    likeVM: LikeViewModel = hiltViewModel(),
    actionRootDestinations: ActionRootDestinations,
    blogScreenState: SwipeRefreshScreenState = rememberSwipeRefreshScreenState(
        sizeScrollMore = 80f,
        isRefreshing = postVM.isRequestData,
    )
) {
    val statePost by postVM.listPost.collectAsState()

    LaunchedEffect(key1 = Unit) {
        merge(postVM.messagePost, likeVM.messageLike).collect(blogScreenState::showSnackMessage)
    }

    LaunchedEffect(key1 = Unit) {
        postVM.requestNewPost(false, blogScreenState::scrollToTop)
    }


    BlogScreen(
        stateListPost = statePost,
        isConcatenate = postVM.isConcatenatePost,
        lazyListState = blogScreenState.listState,
        scaffoldState = blogScreenState.scaffoldState,
        swipeState = blogScreenState.swipeState,
        buttonAddIsVisible = !blogScreenState.isScrollInProgress,
        actionBlogScreen = { action ->
            when (action) {
                RELOAD_BLOG -> postVM.requestNewPost(true, blogScreenState::scrollToTop)
                ADD_BLOG -> actionRootDestinations.changeRoot(AddBlogScreenDestination)
                CONCATENATE_BLOG -> postVM.concatenatePost {
                    postVM.concatenatePost(blogScreenState::animateScrollMore)
                }
            }
        },
        actionBlog = { action, post ->
            when (action) {
                DETAILS -> actionRootDestinations.changeRoot(PostDetailsDestination(post.id))
                COMMENT -> actionRootDestinations.changeRoot(
                    PostDetailsDestination(post.id, true)
                )
                SHARE -> sharePost(post.id, blogScreenState.context)
                LIKE -> likeVM.likePost(post)
                DOWNLOAD -> {}
                SAVE -> {}
            }
        })

}


@Composable
private fun BlogScreen(
    isConcatenate: Boolean,
    buttonAddIsVisible: Boolean,
    lazyListState: LazyListState,
    scaffoldState: ScaffoldState,
    swipeState: SwipeRefreshState,
    stateListPost: Resource<List<Post>>,
    actionBlog: (ActionsPost, SimplePost) -> Unit,
    actionBlogScreen: (ActionBlogScreen) -> Unit
) {

    Box {
        ScaffoldSwipe(
            actionOnRefresh = { actionBlogScreen(RELOAD_BLOG) },
            swipeState = swipeState,
            floatingActionButton = {
                ButtonAdd(
                    isVisible = buttonAddIsVisible,
                    action = { actionBlogScreen(ADD_BLOG) }
                )
            }
        ) {
            ListPost(
                actionBlog = actionBlog,
                listState = lazyListState,
                stateListPost = stateListPost,
                isConcatenate = isConcatenate,
                modifier = Modifier.padding(it),
                actionBottomReached = { actionBlogScreen(CONCATENATE_BLOG) }
            )
        }

        CustomSnackBar(
            hostState = scaffoldState.snackbarHostState,
            modifier = Modifier
                .padding(vertical = 20.dp)
                .align(Alignment.TopCenter)
        )
    }

}

@Composable
private fun ListPost(
    isConcatenate: Boolean,
    listState: LazyListState,
    modifier: Modifier = Modifier,
    actionBottomReached: () -> Unit,
    stateListPost: Resource<List<Post>>,
    actionBlog: (ActionsPost, SimplePost) -> Unit,
    spaceBetweenItems: Dp = 10.dp,
    contentPadding: PaddingValues = PaddingValues(4.dp)
) {

    LaunchedEffect(key1 = Unit) {
        UploadDataServices.finishUploadSuccess.collect {
            delay(200)
            if (listState.firstVisibleItemIndex != 0) {
                listState.animateScrollToItem(0)
            }
        }
    }
    when (stateListPost) {
        Resource.Failure -> ListEmptyBlog(modifier = modifier)
        Resource.Loading -> ListLoadBlog(
            modifier = modifier,
            contentPadding = contentPadding,
            spaceBetweenItems = spaceBetweenItems
        )
        is Resource.Success -> {
            if (stateListPost.data.isEmpty()) {
                ListEmptyBlog(modifier = modifier)
            } else {
                ListSuccessBlog(
                    modifier = modifier,
                    listState = listState,
                    actionBlog = actionBlog,
                    isConcatenate = isConcatenate,
                    listPost = stateListPost.data,
                    contentPadding = contentPadding,
                    spaceBetweenItems = spaceBetweenItems,
                    actionBottomReached = actionBottomReached
                )
            }
        }
    }
}


private fun sharePost(idPost: String, context: Context) {
    val intent = Intent(Intent.ACTION_SEND)
        .putExtra(Intent.EXTRA_TEXT, "https://www.blog-compose.com/post/$idPost")
        .setType("text/plain")
    context.startActivity(
        Intent.createChooser(
            intent,
            context.getString(R.string.title_share_post)
        )
    )
}

