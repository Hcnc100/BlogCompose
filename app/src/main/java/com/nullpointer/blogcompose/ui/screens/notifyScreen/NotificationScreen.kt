package com.nullpointer.blogcompose.ui.screens.notifyScreen

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.nullpointer.blogcompose.R
import com.nullpointer.blogcompose.core.states.Resource
import com.nullpointer.blogcompose.models.notify.Notify
import com.nullpointer.blogcompose.presentation.NotifyViewModel
import com.nullpointer.blogcompose.ui.interfaces.ActionRootDestinations
import com.nullpointer.blogcompose.ui.navigation.HomeNavGraph
import com.nullpointer.blogcompose.ui.navigation.MainTransitions
import com.nullpointer.blogcompose.ui.screens.destinations.PostDetailsDestination
import com.nullpointer.blogcompose.ui.screens.emptyScreen.EmptyScreen
import com.nullpointer.blogcompose.ui.screens.states.SwipeRefreshScreenState
import com.nullpointer.blogcompose.ui.screens.states.rememberSwipeRefreshScreenState
import com.nullpointer.blogcompose.ui.share.CircularProgressAnimation
import com.nullpointer.blogcompose.ui.share.OnBottomReached
import com.ramcosta.composedestinations.annotation.Destination


@HomeNavGraph
@Destination(style = MainTransitions::class)
@Composable
fun NotifyScreen(
    notifyVM: NotifyViewModel = hiltViewModel(),
    actionRootDestinations: ActionRootDestinations,
    notifyScreenState: SwipeRefreshScreenState = rememberSwipeRefreshScreenState(isRefreshing = notifyVM.stateRequestNotify)
) {
    // * states
    val stateListNotify by notifyVM.listNotify.collectAsState()

    LaunchedEffect(key1 = Unit) {
        notifyVM.messageNotify.collect(notifyScreenState::showSnackMessage)
    }
    // * create swipe refresh to force get new data, adn request more content if swipe to end
    SwipeRefresh(
        state = notifyScreenState.swipeState,
        onRefresh = { notifyVM.requestLastNotify(true) },
    ) {
        Scaffold(
            scaffoldState = notifyScreenState.scaffoldState,
            bottomBar = { CircularProgressAnimation(notifyVM.stateConcatNotify) }
        ) {

            when (stateListNotify) {
                Resource.Loading -> LoadingNotify()
                Resource.Failure -> EmptyScreen(
                    resourceRaw = R.raw.empty3,
                    emptyText = stringResource(R.string.message_empty_notify)
                )
                is Resource.Success -> {
                    val listNotify = (stateListNotify as Resource.Success<List<Notify>>).data

                    if (listNotify.isEmpty()) {
                        EmptyScreen(
                            resourceRaw = R.raw.empty3,
                            emptyText = stringResource(R.string.message_empty_notify)
                        )
                    } else {
                        ListSwipeNotify(
                            listState = notifyScreenState.listState,
                            listNotify = listNotify,
                            actionBottomReached = notifyVM::concatenateNotify,
                            actionClick = { notify ->
                                actionRootDestinations.changeRoot(
                                    PostDetailsDestination(
                                        notify.idPost,
                                        false
                                    )
                                )
                                if (!notify.isOpen) notifyVM.openNotifications(notify)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ListSwipeNotify(
    listNotify: List<Notify>,
    listState: LazyListState,
    actionBottomReached: () -> Unit,
    actionClick: (notify: Notify) -> Unit,
) {
    // * list to show notifications
    LazyColumn(
        state = listState
    ) {
        // * all notifications
        items(listNotify.size) { index ->
            ItemNotify(
                notify = listNotify[index],
                actionClick = actionClick
            )
        }
    }
    // * when go to the finish list, request more notifications
    if (listState.layoutInfo.visibleItemsInfo.size < listState.layoutInfo.totalItemsCount) {
        listState.OnBottomReached(0) {
            actionBottomReached()
        }
    }
}



