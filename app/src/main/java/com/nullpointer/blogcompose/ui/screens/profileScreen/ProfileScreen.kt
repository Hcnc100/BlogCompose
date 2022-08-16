package com.nullpointer.blogcompose.ui.screens.profileScreen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
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
import com.nullpointer.blogcompose.ui.screens.emptyScreen.AnimationScreen
import com.nullpointer.blogcompose.ui.screens.profileScreen.components.FailedProfilePost
import com.nullpointer.blogcompose.ui.screens.profileScreen.components.ItemMyPost
import com.nullpointer.blogcompose.ui.screens.states.ProfileScreenState
import com.nullpointer.blogcompose.ui.screens.states.rememberProfileScreenState
import com.nullpointer.blogcompose.ui.share.ButtonAdd
import com.nullpointer.blogcompose.ui.share.CircularProgressAnimation
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
            ) {
                Box(modifier = Modifier.padding(it).fillMaxSize()) {
                    ProfileScreen(
                        user = currentUser,
                        listPostState = stateListPost,
                        gridState = profileScreenState.listState,
                        actionDetails = {idPost->
                            actionRootDestinations.changeRoot(
                                PostDetailsDestination(idPost)
                            )
                        },
                        actionProfile = { action ->
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
                    CircularProgressAnimation(
                        isVisible = myPostViewModel.stateConcatMyPost,
                        modifier = Modifier.align(
                            Alignment.BottomCenter
                        )
                    )
                }

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
        columns = GridCells.Adaptive(120.dp),
        modifier = Modifier.fillMaxWidth()
        ) {

        item(
            span = { GridItemSpan(maxLineSpan) },
            key = user.idUser
        ) {
            HeaderUser(
                user = user,
                actionEditPhoto = { actionProfile(ActionMyProfile.SHOW_MODAL) },
                actionSettings = { actionProfile(ActionMyProfile.GO_SETTINGS) }
            )
        }
        when (listPostState) {
            Resource.Failure -> item(key = "failed-user" ) { FailedProfilePost() }
            Resource.Loading -> items(count = 20, key = { it }) { ItemLoadingMyPost() }
            is Resource.Success -> {
                if (listPostState.data.isEmpty()) {
                    item(key = "empty-my post" , span = { GridItemSpan(maxLineSpan)}) {
                        AnimationScreen(
                            resourceRaw = R.raw.empty5,
                            emptyText = stringResource(id = R.string.message_empty_my_post),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(350.dp)
                        )
                    }
                } else {
                    items(
                        count = listPostState.data.size,
                        key = { listPostState.data[it].id }
                    ) { index ->
                        ItemMyPost(
                            post = listPostState.data[index],
                            modifier = Modifier.animateItemPlacement(),
                            actionDetails = actionDetails
                        )
                    }
                }
            }
        }
    }
    if (listPostState is Resource.Success)
        gridState.OnBottomReached(
            buffer = 0,
            onLoadMore = { actionProfile(ActionMyProfile.LOAD_MORE) })

}


