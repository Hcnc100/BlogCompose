package com.nullpointer.blogcompose.ui.screens.blogScreen

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.nullpointer.blogcompose.R
import com.nullpointer.blogcompose.core.states.Resource
import com.nullpointer.blogcompose.models.posts.ActionsPost
import com.nullpointer.blogcompose.models.posts.ActionsPost.*
import com.nullpointer.blogcompose.models.posts.Post
import com.nullpointer.blogcompose.models.posts.SimplePost
import com.nullpointer.blogcompose.presentation.LikeViewModel
import com.nullpointer.blogcompose.presentation.PostViewModel
import com.nullpointer.blogcompose.ui.interfaces.ActionRootDestinations
import com.nullpointer.blogcompose.ui.navigation.HomeNavGraph
import com.nullpointer.blogcompose.ui.navigation.MainTransitions
import com.nullpointer.blogcompose.ui.screens.destinations.AddBlogScreenDestination
import com.nullpointer.blogcompose.ui.screens.emptyScreen.EmptyScreen
import com.nullpointer.blogcompose.ui.screens.states.SwipeRefreshScreenState
import com.nullpointer.blogcompose.ui.screens.states.rememberSwipeRefreshScreenState
import com.nullpointer.blogcompose.ui.share.ButtonAdd
import com.nullpointer.blogcompose.ui.share.CircularProgressAnimation
import com.ramcosta.composedestinations.annotation.Destination

@HomeNavGraph(start = true)
@Destination(style = MainTransitions::class)
@Composable
fun BlogScreen(
    postVM: PostViewModel = hiltViewModel(),
    likeVM: LikeViewModel = hiltViewModel(),
    blogScreenState: SwipeRefreshScreenState = rememberSwipeRefreshScreenState(postVM.stateRequestData),
    actionRootDestinations: ActionRootDestinations
) {
    val statePost by postVM.listPost.collectAsState()

    LaunchedEffect(key1 = Unit) {
        postVM.messagePost.collect(blogScreenState::showSnackMessage)
    }

    LaunchedEffect(key1 = Unit) {
        postVM.messagePost.collect(blogScreenState::showSnackMessage)
    }

    SwipeRefresh(
        modifier = Modifier.fillMaxSize(),
        state = blogScreenState.swipeState,
        onRefresh = { postVM.requestNewPost(true) }) {
        Scaffold(
            bottomBar = { CircularProgressAnimation(postVM.stateConcatData) },
            floatingActionButton = {
                ButtonAdd(isScrollInProgress = blogScreenState.isScrollInProgress) {
                    actionRootDestinations.changeRoot(AddBlogScreenDestination)
                }
            }
        ) {
            when (statePost) {
                Resource.Failure -> EmptyScreen(
                    resourceRaw = R.raw.empty1, emptyText = stringResource(
                        id = R.string.message_empty_post
                    )
                )
                Resource.Loading -> LoadingPost()
                is Resource.Success -> {
                    val listPost= (statePost as Resource.Success<List<Post>>).data
                    if (listPost.isEmpty()) {
                        EmptyScreen(
                            resourceRaw = R.raw.empty1, emptyText = stringResource(
                                id = R.string.message_empty_post
                            )
                        )
                    } else {
                        ListPost(
                            listPost = listPost,
                            listState = blogScreenState.listState,
                            actionBlog = { action, post ->
                                when (action) {
                                    DETAILS -> TODO()
                                    SHARE -> sharePost(post.id, blogScreenState.context)
                                    LIKE -> TODO()
                                    DOWNLOAD -> TODO()
                                    SAVE -> TODO()
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ListPost(
    listPost: List<Post>,
    listState: LazyListState,
    actionBlog: (ActionsPost, SimplePost) -> Unit
) {
    LazyColumn(state = listState) {
        items(listPost.size) { index ->
            BlogItem(
                post = listPost[index],
                actionBlog = actionBlog,
            )
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

