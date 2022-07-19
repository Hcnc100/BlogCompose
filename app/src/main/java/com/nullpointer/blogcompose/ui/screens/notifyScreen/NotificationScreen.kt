package com.nullpointer.blogcompose.ui.screens.notifyScreen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import com.nullpointer.blogcompose.models.notify.Notify
import com.nullpointer.blogcompose.presentation.NotifyViewModel
import com.nullpointer.blogcompose.services.notfication.MyFirebaseMessagingService
import com.nullpointer.blogcompose.services.uploadImg.UploadDataServices
import com.nullpointer.blogcompose.ui.interfaces.ActionRootDestinations
import com.nullpointer.blogcompose.ui.navigation.HomeNavGraph
import com.nullpointer.blogcompose.ui.navigation.MainTransitions
import com.nullpointer.blogcompose.ui.screens.destinations.PostDetailsDestination
import com.nullpointer.blogcompose.ui.screens.emptyScreen.AnimationScreen
import com.nullpointer.blogcompose.ui.screens.states.SwipeRefreshScreenState
import com.nullpointer.blogcompose.ui.screens.states.rememberSwipeRefreshScreenState
import com.nullpointer.blogcompose.ui.share.CircularProgressAnimation
import com.nullpointer.blogcompose.ui.share.OnBottomReached
import com.ramcosta.composedestinations.annotation.Destination
import kotlinx.coroutines.delay


@HomeNavGraph
@Destination(style = MainTransitions::class)
@Composable
fun NotifyScreen(
    notifyVM: NotifyViewModel = hiltViewModel(),
    actionRootDestinations: ActionRootDestinations,
    notifyScreenState: SwipeRefreshScreenState = rememberSwipeRefreshScreenState(
        isRefreshing = notifyVM.stateRequestNotify,
        sizeScrollMore = 120f
    )
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
                Resource.Failure -> AnimationScreen(
                    resourceRaw = R.raw.empty3,
                    emptyText = stringResource(R.string.message_empty_notify),
                    modifier = Modifier.padding(it).fillMaxSize()
                )
                is Resource.Success -> {
                    val listNotify = (stateListNotify as Resource.Success<List<Notify>>).data

                    if (listNotify.isEmpty()) {
                        AnimationScreen(
                            modifier = Modifier.padding(it).fillMaxSize(),
                            resourceRaw = R.raw.empty3,
                            emptyText = stringResource(R.string.message_empty_notify)
                        )
                    } else {
                        ListSwipeNotify(
                            listState = notifyScreenState.listState,
                            listNotify = listNotify,
                            actionBottomReached = {
                                notifyVM.concatenateNotify(callbackSuccess = notifyScreenState::animateScrollMore)
                            },
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ListSwipeNotify(
    listNotify: List<Notify>,
    listState: LazyListState,
    actionBottomReached: () -> Unit,
    modifier: Modifier = Modifier,
    actionClick: (notify: Notify) -> Unit,
) {

    LaunchedEffect(key1 = Unit) {
        MyFirebaseMessagingService.notifyServices.collect {
            delay(200)
            if (listState.firstVisibleItemIndex != 0) {
                listState.animateScrollToItem(0)
            }
        }
    }

    // * list to show notifications
    LazyColumn(
        state = listState,
        modifier = modifier
    ) {
        // * all notifications
        items(
            listNotify.size,
            key = { index -> listNotify[index].id }
        ) { index ->
            ItemNotify(
                notify = listNotify[index],
                actionClick = actionClick,
                modifier = Modifier.animateItemPlacement()
            )
        }
    }
    listState.OnBottomReached(0) {
        actionBottomReached()
    }

}



