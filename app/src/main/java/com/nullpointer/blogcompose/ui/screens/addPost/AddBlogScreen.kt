package com.nullpointer.blogcompose.ui.screens.addPost

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.nullpointer.blogcompose.R
import com.nullpointer.blogcompose.actions.AddBlogAction
import com.nullpointer.blogcompose.actions.AddBlogAction.*
import com.nullpointer.blogcompose.core.delegates.PropertySavableImg
import com.nullpointer.blogcompose.core.delegates.PropertySavableString
import com.nullpointer.blogcompose.core.utils.getGrayColor
import com.nullpointer.blogcompose.core.utils.isSuccess
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
                SEND_POST -> {
                    addBlogVM.createPostValidate(
                        addBlogState.context,
                        rootDestinations::backDestination
                    )
                }
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


@Composable
private fun ImageNewBlog(
    imgBlog: PropertySavableImg,
    actionEditImg: () -> Unit,
    context: Context = LocalContext.current
) {

    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(context).data(imgBlog.value).crossfade(true).build(),
        placeholder = painterResource(id = R.drawable.ic_image),
        error = painterResource(id = R.drawable.ic_broken_image),
    )

    Card {
        Box(contentAlignment = Alignment.Center) {
            Image(
                painter = if (imgBlog.isNotEmpty) painter else painterResource(id = R.drawable.ic_image),
                contentDescription = stringResource(id = R.string.description_img_loaded),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                colorFilter = if (painter.isSuccess) null else ColorFilter.tint(getGrayColor()),
            )
            if (imgBlog.isCompress)
                CircularProgressIndicator(
                    modifier = Modifier.size(50.dp),
                    color = MaterialTheme.colors.primary,
                    strokeWidth = 5.dp
                )
            FloatingActionButton(
                onClick = actionEditImg,
                modifier = Modifier
                    .padding(10.dp)
                    .size(40.dp)
                    .align(Alignment.BottomEnd)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_edit),
                    contentDescription = stringResource(
                        id = R.string.description_edit_img_post
                    )
                )
            }
        }
    }
}
