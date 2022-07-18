package com.nullpointer.blogcompose.ui.screens.dataUser

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.nullpointer.blogcompose.R
import com.nullpointer.blogcompose.presentation.AuthViewModel
import com.nullpointer.blogcompose.presentation.RegistryViewModel
import com.nullpointer.blogcompose.ui.navigation.RootNavGraph
import com.nullpointer.blogcompose.ui.screens.states.SelectImageScreenState
import com.nullpointer.blogcompose.ui.screens.states.rememberSelectImageScreenState
import com.nullpointer.blogcompose.ui.share.EditableTextSavable
import com.nullpointer.blogcompose.ui.share.SelectImgButtonSheet
import com.nullpointer.blogcompose.ui.share.SimpleToolbar
import com.ramcosta.composedestinations.annotation.Destination

@OptIn(ExperimentalMaterialApi::class)
@RootNavGraph
@Destination
@Composable
fun DataUserScreen(
    authViewModel: AuthViewModel,
    registryViewModel: RegistryViewModel = hiltViewModel(),
    dataScreenState: SelectImageScreenState = rememberSelectImageScreenState()
) {

    LaunchedEffect(key1 = Unit) {
        registryViewModel.registryMessage.collect(dataScreenState::showSnackMessage)
    }
    BackHandler(dataScreenState.isShowModal) {
        dataScreenState.hiddenModal()
    }

    ModalBottomSheetLayout(
        sheetState = dataScreenState.modalBottomSheetState,
        sheetContent = {
            SelectImgButtonSheet(
                isVisible = dataScreenState.isShowModal,
                actionHidden = dataScreenState::hiddenModal,
                actionBeforeSelect = { uri ->
                    uri?.let {
                        registryViewModel.imageProfile.changeValue(it, dataScreenState.context)
                    }
                    dataScreenState.hiddenModal()
                }
            )
        },
    ) {
        Scaffold(
            scaffoldState = dataScreenState.scaffoldState,
            topBar = {
                SimpleToolbar(title = stringResource(id = R.string.title_profile))
            },
            floatingActionButtonPosition = FabPosition.Center,
            floatingActionButton = {
                ButtonRegistryStatus(isEnabled = registryViewModel.isDataValid) {
                    dataScreenState.hiddenKeyBoard()
                    registryViewModel.getUpdatedUser()?.let {
                        authViewModel.createNewUser(it)
                    }
                }
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                PhotoProfile(
                    urlImg = registryViewModel.imageProfile.value,
                    actionChangePhoto = dataScreenState::showModal,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    isCompress = registryViewModel.imageProfile.isCompress
                )
                Spacer(modifier = Modifier.height(40.dp))
                EditableTextSavable(
                    valueProperty = registryViewModel.nameUser,
                )
            }
        }
    }
    if(authViewModel.creatingUser) CreatingDialog()
}

@Composable
private fun PhotoProfile(
    modifier: Modifier = Modifier,
    urlImg: Uri,
    isCompress: Boolean,
    actionChangePhoto: () -> Unit
) {

    Box(modifier = modifier) {
        Card(shape = CircleShape) {
            Box(contentAlignment = Alignment.Center) {
                SubcomposeAsyncImage(
                    model = urlImg,
                    contentDescription = stringResource(id = R.string.description_image_profile),
                    modifier = Modifier.size(180.dp),
                    contentScale = ContentScale.Crop
                ) {

                    when {
                        painter.state is AsyncImagePainter.State.Success -> SubcomposeAsyncImageContent()
                        painter.state is AsyncImagePainter.State.Error && urlImg != Uri.EMPTY -> {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_broken_image),
                                contentDescription = stringResource(id = R.string.description_error_load_img),
                                modifier = Modifier.padding(40.dp)
                            )
                        }
                        else -> Icon(
                            painter = painterResource(id = R.drawable.ic_person),
                            contentDescription = stringResource(id = R.string.description_image_profile),
                            modifier = Modifier.padding(40.dp)
                        )
                    }
                }

                if (isCompress)
                    CircularProgressIndicator(
                        modifier = Modifier.size(30.dp),
                        color = MaterialTheme.colors.primary,
                        strokeWidth = 4.dp
                    )

            }
        }
        FloatingActionButton(
            onClick = actionChangePhoto,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(15.dp)
                .size(40.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_edit),
                contentDescription = stringResource(
                    id = R.string.change_image_user
                )
            )
        }
    }


}





@Composable
private fun ButtonRegistryStatus(
    isEnabled: Boolean,
    modifier: Modifier = Modifier,
    actionClick: () -> Unit
) {
    Button(
        modifier = modifier,
        onClick = actionClick,
        enabled = isEnabled
    ) {
        Text(
           stringResource(R.string.text_button_registry),
            modifier = Modifier.padding(horizontal = 20.dp)
        )
    }
}