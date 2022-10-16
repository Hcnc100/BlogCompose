package com.nullpointer.blogcompose.ui.screens.notifyScreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
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
import com.nullpointer.blogcompose.actions.ActionNotify
import com.nullpointer.blogcompose.actions.ActionNotify.CONCATENATE_NOTIFY
import com.nullpointer.blogcompose.actions.ActionNotify.RELOAD_NOTIFY
import com.nullpointer.blogcompose.core.states.Resource
import com.nullpointer.blogcompose.models.notify.Notify
import com.nullpointer.blogcompose.presentation.NotifyViewModel
import com.nullpointer.blogcompose.services.notfication.MyFirebaseMessagingService
import com.nullpointer.blogcompose.ui.interfaces.ActionRootDestinations
import com.nullpointer.blogcompose.ui.navigation.HomeNavGraph
import com.nullpointer.blogcompose.ui.navigation.MainTransitions
import com.nullpointer.blogcompose.ui.screens.destinations.PostDetailsDestination
import com.nullpointer.blogcompose.ui.screens.notifyScreen.components.lists.EmptyListNotify
import com.nullpointer.blogcompose.ui.screens.notifyScreen.components.lists.LoadListNotify
import com.nullpointer.blogcompose.ui.screens.notifyScreen.components.lists.SuccessListNotify
import com.nullpointer.blogcompose.ui.screens.states.SwipeRefreshScreenState
import com.nullpointer.blogcompose.ui.screens.states.rememberSwipeRefreshScreenState
import com.nullpointer.blogcompose.ui.share.CustomSnackBar
import com.nullpointer.blogcompose.ui.share.ScaffoldSwipe
import com.ramcosta.composedestinations.annotation.Destination
import kotlinx.coroutines.delay


@HomeNavGraph
@Destination(style = MainTransitions::class)
@Composable
fun NotifyScreen(
    notifyVM: NotifyViewModel = hiltViewModel(),
    actionRootDestinations: ActionRootDestinations,
    notifyScreenState: SwipeRefreshScreenState = rememberSwipeRefreshScreenState(
        isRefreshing = notifyVM.isRequestNotify,
        sizeScrollMore = 120f
    )
) {
    // * states
    val stateListNotify by notifyVM.listNotify.collectAsState()

    LaunchedEffect(key1 = Unit) {
        notifyVM.messageNotify.collect(notifyScreenState::showSnackMessage)
    }



    NotificationScreen(
        stateListNotify = stateListNotify,
        lazyListState = notifyScreenState.listState,
        scaffoldState = notifyScreenState.scaffoldState,
        isConcatenateNotify = notifyVM.isConcatNotify,
        swipeRefreshState = notifyScreenState.swipeState,
        actionClickNotify = { notify ->
            actionRootDestinations.changeRoot(
                PostDetailsDestination(notify.idPost, false)
            )
            if (!notify.isOpen) notifyVM.openNotifications(notify)
        },
        actionNotify = { action ->
            when (action) {
                RELOAD_NOTIFY -> notifyVM.requestLastNotify(true)
                CONCATENATE_NOTIFY -> notifyVM.concatenateNotify(notifyScreenState::animateScrollMore)
            }
        }
    )
}

@Composable
fun NotificationScreen(
    lazyListState: LazyListState,
    scaffoldState: ScaffoldState,
    isConcatenateNotify: Boolean,
    swipeRefreshState: SwipeRefreshState,
    actionNotify: (ActionNotify) -> Unit,
    stateListNotify: Resource<List<Notify>>,
    actionClickNotify: (notify: Notify) -> Unit
) {
    Box {
        ScaffoldSwipe(
            swipeState = swipeRefreshState,
            actionOnRefresh = { actionNotify(RELOAD_NOTIFY) }
        ) {
            ListNotify(
                listState = lazyListState,
                actionClick = actionClickNotify,
                stateListNotify = stateListNotify,
                isConcatenateNotify = isConcatenateNotify,
                actionBottomReached = { actionNotify(CONCATENATE_NOTIFY) },
                modifier = Modifier
                    .padding(it)
                    .fillMaxSize()
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
private fun ListNotify(
    listState: LazyListState,
    isConcatenateNotify: Boolean,
    modifier: Modifier = Modifier,
    sizeBetweenItems: Dp = 5.dp,
    actionBottomReached: () -> Unit,
    stateListNotify: Resource<List<Notify>>,
    actionClick: (notify: Notify) -> Unit,
    contentPadding: PaddingValues = PaddingValues(4.dp)
) {

    LaunchedEffect(key1 = Unit) {
        MyFirebaseMessagingService.notifyServices.collect {
            delay(200)
            if (listState.firstVisibleItemIndex != 0) {
                listState.animateScrollToItem(0)
            }
        }
    }

    when (stateListNotify) {
        Resource.Failure -> EmptyListNotify(modifier = modifier)
        Resource.Loading -> LoadListNotify(
            modifier = modifier,
            sizeBetweenItems = sizeBetweenItems,
            contentPadding = contentPadding
        )
        is Resource.Success -> {
            if (stateListNotify.data.isEmpty()) {
                EmptyListNotify(modifier = modifier)
            } else {
                SuccessListNotify(
                    modifier = modifier,
                    lazyListState = listState,
                    listNotify = stateListNotify.data,
                    contentPadding = contentPadding,
                    actionClickNotify = actionClick,
                    sizeBetweenItems = sizeBetweenItems,
                    actionRequestMore = actionBottomReached,
                    isConcatenateNotify = isConcatenateNotify
                )
            }
        }
    }

}



