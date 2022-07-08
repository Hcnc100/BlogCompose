package com.nullpointer.blogcompose.ui.screens.homeScreen.notifyScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.nullpointer.blogcompose.R
import com.nullpointer.blogcompose.core.states.Resource
import com.nullpointer.blogcompose.core.utils.TimeUtils
import com.nullpointer.blogcompose.models.notify.Notify
import com.nullpointer.blogcompose.models.notify.TypeNotify
import com.nullpointer.blogcompose.models.notify.TypeNotify.COMMENT
import com.nullpointer.blogcompose.models.notify.TypeNotify.LIKE
import com.nullpointer.blogcompose.presentation.NotifyViewModel
import com.nullpointer.blogcompose.ui.navigation.HomeNavGraph
import com.nullpointer.blogcompose.ui.navigation.MainTransitions
import com.nullpointer.blogcompose.ui.screens.emptyScreen.EmptyScreen
import com.nullpointer.blogcompose.ui.share.CircularProgressAnimation
import com.nullpointer.blogcompose.ui.share.ImagePost
import com.nullpointer.blogcompose.ui.share.ImageProfile
import com.nullpointer.blogcompose.ui.share.OnBottomReached
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator


@HomeNavGraph
@Destination(style = MainTransitions::class)
@Composable
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
    val context= LocalContext.current
    LaunchedEffect(notifyMessage) {
        notifyMessage.collect {
            scaffoldState.snackbarHostState.showSnackbar(
                context.getString(it)
            )
        }
    }
    // * create swipe refresh to force get new data, adn request more content if swipe to end
    SwipeRefresh(
        state = SwipeRefreshState(stateLoading.value is Resource.Loading),
        onRefresh = { notifyVM.requestLastNotify(true) },
    ) {
        Scaffold(scaffoldState = scaffoldState,
            bottomBar = {
                CircularProgressAnimation(stateConcatenate.value is Resource.Loading)
            }
        ) {
            ListSwipeNotify(
                listNotify = stateListNotify.value,
                actionBottomReached = notifyVM::concatenateNotify
            ) { notify ->
                // * when click in notification so, update inner database and
                // * remote database, and send to post details
//                navigator.navigate(PostDetailsDestination(notify.idPost, false))
                if (!notify.isOpen) notifyVM.openNotifications(notify)
            }
        }
    }

}

@Composable
fun ListSwipeNotify(
    listNotify: List<Notify>?,
    actionBottomReached: () -> Unit,
    actionClick: (notify: Notify) -> Unit,
) {
    val listState = rememberLazyListState()

    if (listNotify != null) {
        // ? show empty screen when no has notifications
        if (listNotify.isEmpty()) {
            EmptyScreen(resourceRaw = R.raw.empty3,
                emptyText = stringResource(R.string.message_empty_notify))
        } else {
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

                Box(
                    modifier = Modifier
                        .weight(2f)
                        .size(60.dp),
                ) {
                    ImageProfile(
                        urlImgProfile = notify.userInNotify?.urlImg.toString(),
                        paddingLoading = 10.dp,
                        modifier = Modifier
                            .fillMaxSize(),
                        contentDescription = stringResource(R.string.description_user_notify)
                    )
                    Card(
                        backgroundColor = when (notify.type) {
                            LIKE -> Color.Red
                            COMMENT -> Color.DarkGray
                        },
                        shape = CircleShape,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(22.dp)
                    ) {
                        Image(
                            painter = painterResource(id = when (notify.type) {
                                LIKE -> R.drawable.ic_fav
                                COMMENT -> R.drawable.ic_comment
                            }),
                            contentDescription = stringResource(R.string.description_icon_notify_indicate),
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(3.dp)
                        )
                    }

                }

                Spacer(modifier = Modifier.width(10.dp))

                TextNotifyInfo(modifier = Modifier.weight(5f),
                    nameLiked = notify.userInNotify?.nameUser.toString(),
                    timeStamp = notify.timestamp?.time ?: 0,
                    typeNotify = notify.type)

                ImagePost(
                    urlImgPost = notify.urlImgPost,
                    paddingLoading = 0.dp,
                    contentDescription = stringResource(R.string.description_post_img_notify),
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
    typeNotify: TypeNotify,
) {
    val textNotify = when (typeNotify) {
        LIKE -> stringResource(id = R.string.message_notify_liked,nameLiked)
        COMMENT -> stringResource(id = R.string.message_notify_comment,nameLiked)
    }
    val context = LocalContext.current
    Column(modifier = modifier) {
        Text(
            text = textNotify,
            style = MaterialTheme.typography.body2,
            fontSize = 13.sp,
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(text = TimeUtils.getTimeAgo(timeStamp, context),
            style = MaterialTheme.typography.caption)
    }
}
