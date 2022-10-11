package com.nullpointer.blogcompose.ui.screens.dataUser

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nullpointer.blogcompose.R
import com.nullpointer.blogcompose.actions.ActionDataUser
import com.nullpointer.blogcompose.actions.ActionDataUser.*
import com.nullpointer.blogcompose.core.delegates.PropertySavableImg
import com.nullpointer.blogcompose.core.delegates.PropertySavableString
import com.nullpointer.blogcompose.presentation.AuthViewModel
import com.nullpointer.blogcompose.presentation.RegistryViewModel
import com.nullpointer.blogcompose.ui.navigation.MainNavGraph
import com.nullpointer.blogcompose.ui.screens.states.SelectImageScreenState
import com.nullpointer.blogcompose.ui.screens.states.rememberSelectImageScreenState
import com.nullpointer.blogcompose.ui.share.CustomSnackBar
import com.nullpointer.blogcompose.ui.share.EditableImage
import com.nullpointer.blogcompose.ui.share.EditableTextSavable
import com.nullpointer.blogcompose.ui.share.ScaffoldModal
import com.ramcosta.composedestinations.annotation.Destination

@OptIn(ExperimentalMaterialApi::class)
@MainNavGraph
@Destination
@Composable
fun DataUserScreen(
    authViewModel: AuthViewModel,
    registryViewModel: RegistryViewModel = hiltViewModel(),
    dataScreenState: SelectImageScreenState = rememberSelectImageScreenState(
        actionChangeImage = registryViewModel.imageProfile::changeValue
    )
) {

    LaunchedEffect(key1 = Unit) {
        registryViewModel.registryMessage.collect(dataScreenState::showSnackMessage)
    }

    DataUserScreen(
        nameUser = registryViewModel.nameUser,
        imgUser = registryViewModel.imageProfile,
        isVisibleModal = dataScreenState.isShowModal,
        sheetState = dataScreenState.modalBottomSheetState,
        hostState = dataScreenState.scaffoldState.snackbarHostState,
        actionDataUser = { action ->
            when (action) {
                SHOW_MODAL -> dataScreenState.showModal()
                HIDDEN_MODAL -> dataScreenState.hiddenModal()
                CREATE_USER -> {
                    dataScreenState.hiddenKeyBoard()
                    registryViewModel.getUpdatedUser()?.let {
                        authViewModel.createNewUser(it)
                    }
                }
            }
        },
        actionSelectImg = dataScreenState::launchSelectImage
    )

    if (authViewModel.isProcessing) CreatingDialog()
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun DataUserScreen(
    isVisibleModal: Boolean,
    hostState: SnackbarHostState,
    imgUser: PropertySavableImg,
    actionSelectImg: (Uri) -> Unit,
    nameUser: PropertySavableString,
    sheetState: ModalBottomSheetState,
    actionDataUser: (ActionDataUser) -> Unit
) {
    ScaffoldModal(
        sheetState = sheetState,
        isVisibleModal = isVisibleModal,
        actionHideModal = { actionDataUser(HIDDEN_MODAL) },
        callBackSelection = actionSelectImg,
        floatingActionButtonPosition = FabPosition.Center,
    ) {
        Box(modifier = Modifier.padding(it)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(20.dp)
                ) {
                    EditableImage(
                        imgUser = imgUser,
                        sizeImage = 180.dp,
                        sizePlaceHolder = 150.dp,
                        actionChangePhoto = { actionDataUser(SHOW_MODAL) },
                        contentDescription = stringResource(id = R.string.description_image_profile),
                        isCircular = true,
                    )
                    EditableTextSavable(
                        singleLine = true,
                        valueProperty = nameUser,
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(
                            onDone = { actionDataUser(CREATE_USER) }
                        ),
                    )
                }


                ButtonRegistryStatus(
                    actionClick = { actionDataUser(CREATE_USER) },
                    modifier = Modifier
                        .padding(15.dp)
                        .align(Alignment.CenterHorizontally),
                )
            }

            CustomSnackBar(
                hostState = hostState, modifier = Modifier
                    .padding(vertical = 70.dp)
                    .align(Alignment.BottomCenter)
            )
        }

    }
}



@Composable
private fun ButtonRegistryStatus(
    modifier: Modifier = Modifier,
    actionClick: () -> Unit
) {
    Button(
        modifier = modifier,
        onClick = actionClick,
    ) {
        Text(
           stringResource(R.string.text_button_registry),
            modifier = Modifier.padding(horizontal = 20.dp)
        )
    }
}