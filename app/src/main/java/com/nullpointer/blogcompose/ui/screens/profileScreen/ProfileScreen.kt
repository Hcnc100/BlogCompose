package com.nullpointer.blogcompose.ui.screens.profileScreen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.GridItemSpan
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
import com.nullpointer.blogcompose.presentation.LikeViewModel
import com.nullpointer.blogcompose.presentation.MyPostViewModel
import com.nullpointer.blogcompose.ui.interfaces.ActionRootDestinations
import com.nullpointer.blogcompose.ui.navigation.HomeNavGraph
import com.nullpointer.blogcompose.ui.navigation.MainTransitions
import com.nullpointer.blogcompose.ui.screens.destinations.AddBlogScreenDestination
import com.nullpointer.blogcompose.ui.screens.emptyScreen.EmptyScreen
import com.nullpointer.blogcompose.ui.screens.states.SelectImageScreenState
import com.nullpointer.blogcompose.ui.screens.states.rememberSelectImageScreenState
import com.nullpointer.blogcompose.ui.share.BackHandler
import com.nullpointer.blogcompose.ui.share.ButtonAdd
import com.nullpointer.blogcompose.ui.share.SelectImgButtonSheet
import com.ramcosta.composedestinations.annotation.Destination

@OptIn(ExperimentalMaterialApi::class)
@HomeNavGraph
@Destination(style = MainTransitions::class)
@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel,
    myPostViewModel: MyPostViewModel = hiltViewModel(),
    likeViewModel: LikeViewModel = hiltViewModel(),
    actionRootDestinations: ActionRootDestinations,
    profileScreenState: SelectImageScreenState = rememberSelectImageScreenState()
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
//                    uri?.let {
//                        registryViewModel.imageProfile.changeValue(it, dataScreenState.context)
//                    }
                    profileScreenState.hiddenModal()
                }
            )
        },
    ) {
        Scaffold(floatingActionButton = {
//            ButtonAdd(isScrollInProgress = profileScreenState.isScrollInProgress) {
//                actionRootDestinations.changeRoot(AddBlogScreenDestination)
//            }
        }) {
            when (stateListPost) {
                Resource.Failure -> EmptyScreen(
                    resourceRaw = R.raw.empty3,
                    emptyText = stringResource(id = R.string.message_empty_post)
                )
                Resource.Loading -> LoadingProfileScreen {
                    HeaderUser(user = currentUser)
                }
                is Resource.Success -> {
                    val listPost = (stateListPost as Resource.Success<List<MyPost>>).data
                    if (listPost.isEmpty()) {
                        EmptyScreen(
                            resourceRaw = R.raw.empty3,
                            emptyText = stringResource(id = R.string.message_empty_post)
                        )
                    } else {
                        GridPost(listPost = listPost) {
                            HeaderUser(user = currentUser)
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
    headerProfile: @Composable () -> Unit
) {
    LazyVerticalGrid(
        contentPadding = PaddingValues(2.dp),
        cells = GridCells.Adaptive(100.dp)
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            headerProfile()
        }
        items(listPost.size) { index ->
            val post = listPost[index]
            Card(
                modifier = Modifier
                    .padding(4.dp)
                    .fillMaxWidth()
                    .aspectRatio(1f),
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
}

@Composable
private fun HeaderUser(
    user: MyUser
) {
    Card {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = ImageRequest
                    .Builder(LocalContext.current)
                    .data(user.urlImg)
                    .transformations(CircleCropTransformation())
                    .crossfade(true)
                    .build(),
                modifier = Modifier.size(150.dp),
                contentDescription = "",
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(15.dp))
            Text(text = user.name)
        }
    }
}