package com.nullpointer.blogcompose.ui.screens.homeScreen.notifyScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberImagePainter
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.nullpointer.blogcompose.R
import com.nullpointer.blogcompose.core.states.Resource
import com.nullpointer.blogcompose.core.utils.TimeUtils
import com.nullpointer.blogcompose.models.Notify
import com.nullpointer.blogcompose.presentation.NotifyViewModel
import com.nullpointer.blogcompose.ui.screens.destinations.PostDetailsDestination
import com.nullpointer.blogcompose.ui.screens.emptyScreen.EmptyScreen
import com.nullpointer.blogcompose.ui.share.CircularProgressAnimation
import com.nullpointer.blogcompose.ui.share.ImageProfile
import com.nullpointer.blogcompose.ui.share.OnBottomReached
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.flow.collect


@Composable
@Destination(navGraph = "homeDestinations")
fun NotifyScreen(
    notifyVM: NotifyViewModel = hiltViewModel(),
    navigator: DestinationsNavigator,
) {
    // * states
    val stateListNotify = notifyVM.listNotify.collectAsState()
    val stateLoading = notifyVM.stateRequest.collectAsState()
    val stateConcatenate = notifyVM.stateConcatenate.collectAsState()
    // * messages
    val notifyMessage = notifyVM.messageNotify
    val scaffoldState = rememberScaffoldState()
    LaunchedEffect(notifyMessage) {
        notifyMessage.collect {
            scaffoldState.snackbarHostState.showSnackbar(it)
        }
    }
    // * create swipe refresh to force get new data, adn request more content if swipe to end
    SwipeRefresh(
        state = SwipeRefreshState(stateLoading.value is Resource.Loading),
        onRefresh = { notifyVM.requestLastNotify(true) },
    ) {
        Scaffold(scaffoldState = scaffoldState) {
            ListSwipeNotify(
                listNotify = stateListNotify.value,
                stateConcatenate = stateConcatenate.value,
                actionBottomReached = notifyVM::concatenateNotify
            ) { notify ->
                // * when click in notification so, update inner database and
                // * remote database, and send to post details
                navigator.navigate(PostDetailsDestination(notify.idPost,false))
                if (!notify.isOpen) notifyVM.openNotifications(notify)
            }
        }
    }

}

@Composable
fun ListSwipeNotify(
    listNotify: List<Notify>?,
    stateConcatenate: Resource<Unit>,
    actionBottomReached: () -> Unit,
    actionClick: (notify: Notify) -> Unit,
) {

    val listState = rememberLazyListState()

    if (listNotify != null) {
        // ? show empty screen when no has notifications
        if (listNotify.isEmpty()) {
            EmptyScreen(resourceRaw = R.raw.empty3,
                emptyText = "No tiene notificaciones")
        } else {
            // * list to show notifications
            LazyColumn {
                // * all notifications
                items(listNotify.size) { index ->
                    ItemNotify(
                        notify = listNotify[index],
                        actionClick = actionClick
                    )
                }
                // * show progress indicator when load new notifications
                // ? this is animate
                item {
                    CircularProgressAnimation(stateConcatenate is Resource.Loading)
                }
            }
            // * when go to the finish list, request more notifications
            listState.OnBottomReached(0) {
                actionBottomReached()
            }
        }
    }

}

@Composable
fun ItemNotify(
    notify: Notify,
    actionClick: (notify: Notify) -> Unit,
) {
    // * change color of items when is open or no
    val modifierColor = Modifier.background(
        if (notify.isOpen) Color.Transparent else MaterialTheme.colors.primary.copy(alpha = 0.5f)
    )
    // * card container
    Card(modifier = Modifier
        .clickable { actionClick(notify) }
        .padding(vertical = 5.dp, horizontal = 5.dp),
        shape = RoundedCornerShape(10.dp)) {
        // * container background color
        Box(modifier = modifierColor) {
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp)) {

                ImageProfile(
                    urlImgProfile = notify.imgUserLiked,
                    paddingLoading = 10.dp,
                    sizeImage = 60.dp,
                    modifier = Modifier.weight(2f),
                )

                Spacer(modifier = Modifier.width(10.dp))

                TextNotifyInfo(modifier = Modifier.weight(5f),
                    nameLiked = notify.nameUserLiked,
                    timeStamp = notify.timestamp?.time ?: 0)

                Image(painter = rememberImagePainter(data = notify.urlImgPost),
                    contentDescription = "",
                    modifier = Modifier
                        .size(60.dp)
                        .weight(2f)
                        .align(Alignment.CenterVertically))

            }
        }

    }
}

@Composable
fun TextNotifyInfo(
    modifier: Modifier,
    nameLiked: String,
    timeStamp: Long,
) {
    val context = LocalContext.current
    Column(modifier = modifier) {
        Text(
            text = "A $nameLiked le gusta tu post",
            style = MaterialTheme.typography.body2,
            fontSize = 13.sp,
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(text = TimeUtils.getTimeAgo(timeStamp, context),
            style = MaterialTheme.typography.caption)
    }
}
