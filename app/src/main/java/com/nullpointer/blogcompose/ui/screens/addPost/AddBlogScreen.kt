package com.nullpointer.blogcompose.ui.screens.addPost

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nullpointer.blogcompose.R
import com.nullpointer.blogcompose.actions.AddBlogAction
import com.nullpointer.blogcompose.actions.AddBlogAction.*
import com.nullpointer.blogcompose.core.delegates.PropertySavableImg
import com.nullpointer.blogcompose.core.delegates.PropertySavableString
import com.nullpointer.blogcompose.ui.interfaces.ActionRootDestinations
import com.nullpointer.blogcompose.ui.navigation.MainNavGraph
import com.nullpointer.blogcompose.ui.screens.addPost.viewModel.AddBlogViewModel
import com.nullpointer.blogcompose.ui.screens.states.SelectImageScreenState
import com.nullpointer.blogcompose.ui.screens.states.rememberSelectImageScreenState
import com.nullpointer.blogcompose.ui.share.EditableImage
import com.nullpointer.blogcompose.ui.share.EditableTextSavable
import com.nullpointer.blogcompose.ui.share.ScaffoldModal
import com.nullpointer.blogcompose.ui.share.ToolbarBack
import com.ramcosta.composedestinations.annotation.Destination

@OptIn(ExperimentalMaterialApi::class)
@MainNavGraph
@Destination
@Composable
fun AddBlogScreen(
    rootDestinations: ActionRootDestinations,
    addBlogVM: AddBlogViewModel = hiltViewModel(),
    addBlogState: SelectImageScreenState = rememberSelectImageScreenState(actionChangeImage = {})
) {
    BackHandler(addBlogState.isShowModal, addBlogState::hiddenModal)
    LaunchedEffect(key1 = Unit) {
        addBlogVM.messageAddBlog.collect(addBlogState::showSnackMessage)
    }

    AddBlogScreen(
        scaffoldState = addBlogState.scaffoldState,
        isShowModal = addBlogState.isShowModal,
        sheetState = addBlogState.modalBottomSheetState,
        imageProperty = addBlogVM.imageBlog,
        descriptionProperty = addBlogVM.description,
        actionAddBlogScreen = { action ->
            when (action) {
                HIDDEN_MODAL -> addBlogState.hiddenModal()
                SHOW_MODAL -> addBlogState.showModal()
                ACTION_BACK -> rootDestinations.backDestination()
                SEND_POST -> addBlogVM.createPostValidate(rootDestinations::backDestination)
            }
        }
    )

}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AddBlogScreen(
    isShowModal: Boolean,
    scaffoldState: ScaffoldState,
    sheetState: ModalBottomSheetState,
    imageProperty: PropertySavableImg,
    descriptionProperty: PropertySavableString,
    actionAddBlogScreen: (AddBlogAction) -> Unit
) {
    ScaffoldModal(
        sheetState = sheetState,
        scaffoldState = scaffoldState,
        isVisibleModal = isShowModal,
        actionHideModal = { actionAddBlogScreen(HIDDEN_MODAL) },
        callBackSelection = imageProperty::changeValue,
        topBar = {
            ToolbarBack(
                title = stringResource(R.string.title_toolbar_add_blog),
                actionBack = { actionAddBlogScreen(ACTION_BACK) }
            )
        },
    ) {
        BoxWithConstraints {
            val sizeWidth = this.maxWidth

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    EditableImage(
                        sizeImage = sizeWidth,
                        imgUser = imageProperty,
                        isCircular = false,
                        sizePlaceHolder = sizeWidth - 50.dp,
                        actionChangePhoto = { actionAddBlogScreen(SHOW_MODAL) }
                    )

                    EditableTextSavable(
                        valueProperty = descriptionProperty,
                        modifier = Modifier
                            .height(150.dp)
                            .width(300.dp),
                    )
                }

                ButtonPublish(
                    actionPublish = { actionAddBlogScreen(SEND_POST) },
                    modifier = Modifier
                        .padding(15.dp)
                        .align(Alignment.CenterHorizontally)
                )
            }
        }

    }
}


@Composable
private fun ButtonPublish(
    actionPublish: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(onClick = actionPublish, modifier = modifier) {
        Icon(
            painterResource(id = R.drawable.ic_publish),
            stringResource(R.string.description_icon_upload)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(stringResource(R.string.text_message_public_post))
    }
}
