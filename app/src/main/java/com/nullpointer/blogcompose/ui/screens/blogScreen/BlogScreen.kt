package com.nullpointer.blogcompose.ui.screens.blogScreen

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.ScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.nullpointer.blogcompose.R
import com.nullpointer.blogcompose.actions.ActionBlogScreen
import com.nullpointer.blogcompose.actions.ActionBlogScreen.*
import com.nullpointer.blogcompose.core.states.Resource
import com.nullpointer.blogcompose.models.posts.Post
import com.nullpointer.blogcompose.models.posts.SimplePost
import com.nullpointer.blogcompose.presentation.LikeViewModel
import com.nullpointer.blogcompose.presentation.PostViewModel
import com.nullpointer.blogcompose.services.uploadImg.UploadDataServices
import com.nullpointer.blogcompose.ui.interfaces.ActionRootDestinations
import com.nullpointer.blogcompose.ui.navigation.HomeNavGraph
import com.nullpointer.blogcompose.ui.navigation.MainTransitions
import com.nullpointer.blogcompose.ui.screens.blogScreen.ActionsPost.*
import com.nullpointer.blogcompose.ui.screens.blogScreen.components.list.ListFailBlog
import com.nullpointer.blogcompose.ui.screens.blogScreen.components.list.ListLoadBlog
import com.nullpointer.blogcompose.ui.screens.blogScreen.components.list.ListSuccessBlog
import com.nullpointer.blogcompose.ui.screens.destinations.AddBlogScreenDestination
import com.nullpointer.blogcompose.ui.screens.destinations.PostDetailsDestination
import com.nullpointer.blogcompose.ui.screens.states.SwipeRefreshScreenState
import com.nullpointer.blogcompose.ui.screens.states.rememberSwipeRefreshScreenState
import com.nullpointer.blogcompose.ui.share.ButtonAdd
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
    blogScreenState: SwipeRefreshScreenState = rememberSwipeRefreshScreenState(
        sizeScrollMore = 80f,
        isRefreshing = postVM.stateRequestData,
    ),
    actionRootDestinations: ActionRootDestinations
) {
    val statePost by postVM.listPost.collectAsState()

    LaunchedEffect(key1 = Unit) {
        merge(postVM.messagePost, likeVM.messageLike).collect(blogScreenState::showSnackMessage)
    }


    BlogScreen(
        stateListPost = statePost,
        isConcatenate = postVM.stateConcatData,
        lazyListState = blogScreenState.listState,
        scaffoldState = blogScreenState.scaffoldState,
        swipeState = blogScreenState.swipeState,
        buttonAddIsVisible = !blogScreenState.isScrollInProgress,
        actionBlogScreen = { action ->
            when (action) {
                RELOAD_BLOG -> postVM.requestNewPost(true)
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
    lazyListState: LazyListState,
    scaffoldState: ScaffoldState,
    swipeState: SwipeRefreshState,
    stateListPost: Resource<List<Post>>,
    actionBlog: (ActionsPost, SimplePost) -> Unit,
    actionBlogScreen: (ActionBlogScreen) -> Unit,
    buttonAddIsVisible: Boolean
) {

    ScaffoldSwipe(
        actionOnRefresh = { actionBlogScreen(RELOAD_BLOG) },
        swipeState = swipeState,
        scaffoldState = scaffoldState,
        floatingActionButton = {
            ButtonAdd(isVisible = buttonAddIsVisible, action = {
                actionBlogScreen(ADD_BLOG)
            })
        }
    ) {
        ListPost(
            listState = lazyListState,
            actionBottomReached = {
                actionBlogScreen(CONCATENATE_BLOG)
            },
            actionBlog = actionBlog,
            stateListPost = stateListPost,
            isConcatenate = isConcatenate
        )
    }
}

@Composable
private fun ListPost(
    listState: LazyListState,
    actionBottomReached: () -> Unit,
    actionBlog: (ActionsPost, SimplePost) -> Unit,
    modifier: Modifier = Modifier,
    stateListPost: Resource<List<Post>>,
    isConcatenate: Boolean
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
        Resource.Failure -> ListFailBlog(modifier = modifier)
        Resource.Loading -> ListLoadBlog(modifier = modifier)
        is Resource.Success -> ListSuccessBlog(
            listPost = stateListPost.data,
            isConcatenate = isConcatenate,
            listState = listState,
            actionBottomReached = actionBottomReached,
            actionBlog = actionBlog,
        )

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

