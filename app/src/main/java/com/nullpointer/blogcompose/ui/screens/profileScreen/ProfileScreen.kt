package com.nullpointer.blogcompose.ui.screens.profileScreen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.GridItemSpan
import androidx.compose.foundation.lazy.LazyGridState
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.nullpointer.blogcompose.R
import com.nullpointer.blogcompose.core.states.Resource
import com.nullpointer.blogcompose.models.posts.MyPost
import com.nullpointer.blogcompose.models.users.MyUser
import com.nullpointer.blogcompose.presentation.AuthViewModel
import com.nullpointer.blogcompose.presentation.MyPostViewModel
import com.nullpointer.blogcompose.services.uploadImg.UploadDataControl
import com.nullpointer.blogcompose.ui.interfaces.ActionRootDestinations
import com.nullpointer.blogcompose.ui.navigation.HomeNavGraph
import com.nullpointer.blogcompose.ui.navigation.MainTransitions
import com.nullpointer.blogcompose.ui.screens.destinations.AddBlogScreenDestination
import com.nullpointer.blogcompose.ui.screens.destinations.ConfigScreenDestination
import com.nullpointer.blogcompose.ui.screens.emptyScreen.AnimationScreen
import com.nullpointer.blogcompose.ui.screens.states.ProfileScreenState
import com.nullpointer.blogcompose.ui.screens.states.rememberProfileScreenState
import com.nullpointer.blogcompose.ui.share.BackHandler
import com.nullpointer.blogcompose.ui.share.ButtonAdd
import com.nullpointer.blogcompose.ui.share.OnBottomReached
import com.nullpointer.blogcompose.ui.share.SelectImgButtonSheet
import com.ramcosta.composedestinations.annotation.Destination

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@HomeNavGraph
@Destination(style = MainTransitions::class)
@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel,
    actionRootDestinations: ActionRootDestinations,
    myPostViewModel: MyPostViewModel = hiltViewModel(),
    profileScreenState: ProfileScreenState = rememberProfileScreenState(
        isRefresh = myPostViewModel.stateRequestMyPost,
        sizeScrollMore = 50f
    )
) {
    // * states
    val stateListPost by myPostViewModel.listMyPost.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()

    BackHandler(profileScreenState.isShowModal) {
        profileScreenState.hiddenModal()
    }


    LaunchedEffect(key1 = Unit) {
        profileScreenState.hiddenModal()
    }

    ModalBottomSheetLayout(
        sheetState = profileScreenState.modalBottomSheetState,
        sheetContent = {
            SelectImgButtonSheet(
                isVisible = profileScreenState.isShowModal,
                actionHidden = profileScreenState::hiddenModal,
                actionBeforeSelect = { uri ->
                    uri?.let {
                        UploadDataControl.startServicesUploadUser(profileScreenState.context, it)
                    }
                    profileScreenState.hiddenModal()
                }
            )
        },
    ) {
        Scaffold(floatingActionButton = {
            ButtonAdd(isScrollInProgress = profileScreenState.isScrollInProgress) {
                actionRootDestinations.changeRoot(AddBlogScreenDestination)
            }
        }) {
            when (stateListPost) {
                Resource.Failure -> AnimationScreen(
                    resourceRaw = R.raw.empty3,
                    emptyText = stringResource(id = R.string.message_empty_post)
                )
                Resource.Loading -> LoadingProfileScreen {
                    HeaderUser(user = currentUser)
                }
                is Resource.Success -> {
                    val listPost = (stateListPost as Resource.Success<List<MyPost>>).data
                    if (listPost.isEmpty()) {
                        AnimationScreen(
                            resourceRaw = R.raw.empty3,
                            emptyText = stringResource(id = R.string.message_empty_post)
                        )
                    } else {
                        GridPost(
                            listPost = listPost,
                            gridState = profileScreenState.listState,
                            actionLoadMore = {
                                myPostViewModel.concatenatePost {
                                    profileScreenState.animateScrollMore()
                                }
                            }
                        ) {
                            HeaderUser(
                                user = currentUser,
                                actionEditPhoto = { profileScreenState.showModal() },
                                actionClickSettings = {
                                    actionRootDestinations.changeRoot(
                                        ConfigScreenDestination
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun GridPost(
    listPost: List<MyPost>,
    gridState: LazyGridState,
    actionLoadMore: () -> Unit,
    headerProfile: @Composable () -> Unit,
) {
    LazyVerticalGrid(
        state = gridState,
        contentPadding = PaddingValues(2.dp),
        cells = GridCells.Adaptive(100.dp)
    ) {
        item(span = { GridItemSpan(maxLineSpan) }, key = { 12345 }) {
            headerProfile()
        }
        items(listPost.size, key = { index ->
            listPost[index].id
        }) { index ->
            val post = listPost[index]
            Card(
                modifier = Modifier
                    .padding(4.dp)
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .animateItemPlacement(),
                shape = RoundedCornerShape(5.dp)
            ) {
                AsyncImage(
                    model = post.urlImage,
                    contentDescription = "",
                    contentScale = ContentScale.Crop
                )
            }
        }
    }

    gridState.OnBottomReached(0) {
        actionLoadMore()
    }

}

@Composable
private fun HeaderUser(
    user: MyUser,
    actionClickSettings: (() -> Unit)? = null,
    actionEditPhoto: (() -> Unit)? = null
) {
    Card {
        Box {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                PhotoProfile(urlImage = user.urlImg, clickEditPhoto = actionEditPhoto)
                Spacer(modifier = Modifier.height(15.dp))
                Text(text = user.name)
            }
            IconButtonSettings(
                modifier = Modifier.align(Alignment.TopEnd),
                actionClickSettings = actionClickSettings
            )
        }
    }
}

@Composable
private fun PhotoProfile(
    modifier: Modifier = Modifier,
    urlImage: String,
    clickEditPhoto: (() -> Unit)? = null
) {
    Box(modifier = modifier) {
        AsyncImage(
            model = ImageRequest
                .Builder(LocalContext.current)
                .data(urlImage)
                .transformations(CircleCropTransformation())
                .crossfade(true)
                .build(),
            modifier = Modifier.size(150.dp),
            contentDescription = "",
            contentScale = ContentScale.Crop
        )
        FloatingActionButton(onClick = {
            clickEditPhoto?.invoke()
        }, modifier = Modifier
            .padding(10.dp)
            .size(35.dp)
            .align(Alignment.BottomEnd)) {
            Icon(painter = painterResource(id = R.drawable.ic_edit), contentDescription = "")
        }
    }

}

@Composable
private fun IconButtonSettings(
    modifier: Modifier = Modifier,
    actionClickSettings: (() -> Unit)? = null
) {
    IconButton(
        onClick = { actionClickSettings?.invoke() },
        modifier = modifier
            .padding(10.dp)
            .size(40.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_settings),
            contentDescription = ""
        )
    }
}