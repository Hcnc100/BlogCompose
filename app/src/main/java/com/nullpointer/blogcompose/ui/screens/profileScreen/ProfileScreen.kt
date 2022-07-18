package com.nullpointer.blogcompose.ui.screens.profileScreen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.GridItemSpan
import androidx.compose.foundation.lazy.LazyGridState
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
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
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.nullpointer.blogcompose.R
import com.nullpointer.blogcompose.core.states.Resource
import com.nullpointer.blogcompose.models.posts.MyPost
import com.nullpointer.blogcompose.models.users.SimpleUser
import com.nullpointer.blogcompose.presentation.AuthViewModel
import com.nullpointer.blogcompose.presentation.MyPostViewModel
import com.nullpointer.blogcompose.services.uploadImg.UploadDataControl
import com.nullpointer.blogcompose.ui.interfaces.ActionRootDestinations
import com.nullpointer.blogcompose.ui.navigation.HomeNavGraph
import com.nullpointer.blogcompose.ui.navigation.MainTransitions
import com.nullpointer.blogcompose.ui.screens.destinations.AddBlogScreenDestination
import com.nullpointer.blogcompose.ui.screens.destinations.ConfigScreenDestination
import com.nullpointer.blogcompose.ui.screens.destinations.PostDetailsDestination
import com.nullpointer.blogcompose.ui.screens.states.ProfileScreenState
import com.nullpointer.blogcompose.ui.screens.states.rememberProfileScreenState
import com.nullpointer.blogcompose.ui.share.*
import com.ramcosta.composedestinations.annotation.Destination


enum class ActionMyProfile {
    LOAD_MORE, GO_SETTINGS, SHOW_MODAL
}

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
        SwipeRefresh(
            state = profileScreenState.swipeState,
            onRefresh = { myPostViewModel.requestNewPost(true) }
        ) {
            Scaffold(
                floatingActionButton = {
                    ButtonAdd(isScrollInProgress = profileScreenState.isScrollInProgress) {
                        actionRootDestinations.changeRoot(AddBlogScreenDestination)
                    }
                },
                bottomBar = { CircularProgressAnimation(myPostViewModel.stateConcatMyPost) }
            ) {
                ProfileScreen(
                    user = currentUser,
                    listPostState = stateListPost,
                    gridState = profileScreenState.listState,
                    actionDetails = { actionRootDestinations.changeRoot(PostDetailsDestination(it)) },
                    actionProfile = {action->
                        when (action) {
                            ActionMyProfile.LOAD_MORE -> myPostViewModel.concatenatePost {
                                profileScreenState.animateScrollMore()
                            }
                            ActionMyProfile.GO_SETTINGS -> actionRootDestinations.changeRoot(
                                ConfigScreenDestination
                            )
                            ActionMyProfile.SHOW_MODAL -> profileScreenState.showModal()
                        }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProfileScreen(
    user: SimpleUser,
    gridState: LazyGridState,
    listPostState: Resource<List<MyPost>>,
    actionDetails: (String) -> Unit,
    actionProfile: (ActionMyProfile) -> Unit,
) {
    LazyVerticalGrid(
        state = gridState,
        cells = GridCells.Adaptive(120.dp)) {
        item(
            span = { GridItemSpan(maxLineSpan) },
            key = { user.idUser }
        ) {
            HeaderUser(
                user = user,
                actionEditPhoto = { actionProfile(ActionMyProfile.SHOW_MODAL) },
                actionSettings = { actionProfile(ActionMyProfile.GO_SETTINGS) }
            )
        }
        when (listPostState) {
            Resource.Failure -> item(key = { "failed-user" }) { FailedProfilePost() }
            Resource.Loading -> items(20, key = { it }) { ItemImageFake() }
            is Resource.Success -> items(
                listPostState.data.size,
                key = { listPostState.data[it].id }) { index ->
                ItemMyPost(
                    post = listPostState.data[index],
                    modifier = Modifier.animateItemPlacement(),
                    actionDetails = actionDetails
                )
            }
        }
    }
    if (listPostState is Resource.Success)
        gridState.OnBottomReached(
            buffer = 0,
            onLoadMore = { actionProfile(ActionMyProfile.LOAD_MORE) })

}


@Composable
private fun FailedProfilePost(
) {
    Column {
        Spacer(modifier = Modifier.height(20.dp))
        LottieContainer(
            animation = R.raw.error3,
            modifier = Modifier
                .size(250.dp)
                .align(Alignment.CenterHorizontally)
        )
        Text(
            text = stringResource(id = R.string.error_load_my_post),
            Modifier.align(Alignment.CenterHorizontally)
        )
    }
}
@Composable
fun HeaderUser(
    user: SimpleUser,
    actionEditPhoto: () -> Unit,
    actionSettings: () -> Unit
) {
    Card {
        Box {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                PhotoProfile(
                    urlImage = user.urlImg,
                    clickEditPhoto = actionEditPhoto
                )
                Spacer(modifier = Modifier.height(15.dp))
                Text(text = user.name)
            }
            IconButtonSettings(
                modifier = Modifier.align(Alignment.TopEnd),
                actionClickSettings = actionSettings
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
            contentDescription = stringResource(id = R.string.description_img_user),
            contentScale = ContentScale.Crop
        )
        FloatingActionButton(onClick = {
            clickEditPhoto?.invoke()
        }, modifier = Modifier
            .padding(10.dp)
            .size(35.dp)
            .align(Alignment.BottomEnd)) {
            Icon(
                painter = painterResource(id = R.drawable.ic_edit),
                contentDescription = stringResource(
                    id = R.string.description_edit_img_user
                )
            )
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
            contentDescription = stringResource(id = R.string.description_settings)
        )
    }
}