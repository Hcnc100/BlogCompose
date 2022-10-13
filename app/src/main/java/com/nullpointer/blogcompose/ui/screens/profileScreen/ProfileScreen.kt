package com.nullpointer.blogcompose.ui.screens.profileScreen

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.nullpointer.blogcompose.actions.ActionMyProfile
import com.nullpointer.blogcompose.actions.ActionMyProfile.*
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
import com.nullpointer.blogcompose.ui.screens.profileScreen.components.lists.ListEmptyMyBlogs
import com.nullpointer.blogcompose.ui.screens.profileScreen.components.lists.ListLoadMyBlogs
import com.nullpointer.blogcompose.ui.screens.profileScreen.components.lists.ListSuccessMyBlogs
import com.nullpointer.blogcompose.ui.screens.states.ProfileScreenState
import com.nullpointer.blogcompose.ui.screens.states.rememberProfileScreenState
import com.nullpointer.blogcompose.ui.share.ButtonAdd
import com.nullpointer.blogcompose.ui.share.CustomSnackBar
import com.nullpointer.blogcompose.ui.share.ScaffoldModalSwipe
import com.ramcosta.composedestinations.annotation.Destination


@OptIn(ExperimentalMaterialApi::class)
@HomeNavGraph
@Destination(style = MainTransitions::class)
@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel,
    context: Context = LocalContext.current,
    actionRootDestinations: ActionRootDestinations,
    myPostViewModel: MyPostViewModel = hiltViewModel(),
    profileScreenState: ProfileScreenState = rememberProfileScreenState(
        isRefresh = myPostViewModel.isRequestMyPost,
        sizeScrollMore = 50f,
        actionChangeImage = {
            UploadDataControl.startServicesUploadUser(context, it)
        }
    )
) {
    // * states
    val stateListPost by myPostViewModel.listMyPost.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()


    LaunchedEffect(key1 = Unit) {
        profileScreenState.hiddenModal()
    }

    LaunchedEffect(key1 = Unit) {
        myPostViewModel.messageMyPosts.collect(profileScreenState::showSnackMessage)
    }


    ProfileScreen(
        user = currentUser,
        listPostState = stateListPost,
        gridState = profileScreenState.listState,
        swipeState = profileScreenState.swipeState,
        isModalVisible = profileScreenState.isShowModal,
        scaffoldState = profileScreenState.scaffoldState,
        sheetState = profileScreenState.modalBottomSheetState,
        isConcatenateMyBlogs = myPostViewModel.isConcatMyPost,
        isAddButtonVisible = !profileScreenState.isScrollInProgress,
        callBackSelectionImg = profileScreenState::launchSelectImage,
        actionDetails = { actionRootDestinations.changeRoot(PostDetailsDestination(it)) },
        actionProfile = { action ->
            when (action) {
                SHOW_MODAL -> profileScreenState.showModal()
                HIDDEN_MODAL -> profileScreenState.hiddenModal()
                REFRESH_BLOGS -> myPostViewModel.requestNewPost(true)
                GO_SETTINGS -> actionRootDestinations.changeRoot(ConfigScreenDestination)
                ADD_NEW_POST -> actionRootDestinations.changeRoot(AddBlogScreenDestination)
                LOAD_MORE -> myPostViewModel.concatenatePost(profileScreenState::animateScrollMore)
            }
        }
    )

}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun ProfileScreen(
    user: SimpleUser,
    isModalVisible: Boolean,
    gridState: LazyGridState,
    isAddButtonVisible: Boolean,
    scaffoldState: ScaffoldState,
    isConcatenateMyBlogs: Boolean,
    swipeState: SwipeRefreshState,
    actionDetails: (String) -> Unit,
    sheetState: ModalBottomSheetState,
    listPostState: Resource<List<MyPost>>,
    callBackSelectionImg: (Uri) -> Unit,
    actionProfile: (ActionMyProfile) -> Unit
) {

    Box {
        ScaffoldModalSwipe(
            swipeState = swipeState,
            sheetState = sheetState,
            isVisibleModal = isModalVisible,
            actionOnRefresh = { actionProfile(REFRESH_BLOGS) },
            actionHideModal = { actionProfile(HIDDEN_MODAL) },
            callBackSelection = callBackSelectionImg,
            floatingActionButton = {
                ButtonAdd(
                    isVisible = isAddButtonVisible,
                    action = { actionProfile(ADD_NEW_POST) })
            }
        ) {
            ListMyBlogs(
                gridState = gridState,
                actionDetails = actionDetails,
                listPostState = listPostState,
                isConcatenateMyBlogs = isConcatenateMyBlogs,
                actionLoadMore = { actionProfile(LOAD_MORE) },
                modifier = Modifier.padding(it)
            ) {
                HeaderUser(
                    user = user,
                    actionEditPhoto = { actionProfile(SHOW_MODAL) },
                    actionSettings = { actionProfile(GO_SETTINGS) }
                )
            }
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
private fun ListMyBlogs(
    gridState: LazyGridState,
    actionLoadMore: () -> Unit,
    modifier: Modifier = Modifier,
    spaceBetweenItems: Dp = 5.dp,
    isConcatenateMyBlogs: Boolean,
    actionDetails: (String) -> Unit,
    listPostState: Resource<List<MyPost>>,
    contextPadding: PaddingValues = PaddingValues(4.dp),
    headerUser: @Composable () -> Unit
) {
    when (listPostState) {
        Resource.Failure -> ListEmptyMyBlogs(
            modifier = modifier,
            header = headerUser
        )
        Resource.Loading -> ListLoadMyBlogs(
            header = headerUser,
            contentPadding = contextPadding,
            spaceBetweenItems = spaceBetweenItems
        )
        is Resource.Success -> {
            if (listPostState.data.isEmpty()) {
                ListEmptyMyBlogs(
                    modifier = modifier,
                    header = headerUser
                )
            } else {
                ListSuccessMyBlogs(
                    header = headerUser,
                    gridState = gridState,
                    listMyPost = listPostState.data,
                    contentPadding = contextPadding,
                    actionClickPost = actionDetails,
                    actionLoadMore = actionLoadMore,
                    spaceBetweenItems = spaceBetweenItems,
                    isConcatenateMyBlog = isConcatenateMyBlogs
                )
            }
        }
    }
}


