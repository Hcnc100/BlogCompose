package com.nullpointer.blogcompose.ui.screens.addPost

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.nullpointer.blogcompose.R
import com.nullpointer.blogcompose.ui.interfaces.ActionRootDestinations
import com.nullpointer.blogcompose.ui.navigation.MainNavGraph
import com.nullpointer.blogcompose.ui.screens.addPost.viewModel.AddBlogViewModel
import com.nullpointer.blogcompose.ui.screens.states.SelectImageScreenState
import com.nullpointer.blogcompose.ui.screens.states.rememberSelectImageScreenState
import com.nullpointer.blogcompose.ui.share.EditableTextSavable
import com.nullpointer.blogcompose.ui.share.SelectImgButtonSheet
import com.nullpointer.blogcompose.ui.share.ToolbarBack
import com.ramcosta.composedestinations.annotation.Destination

@OptIn(ExperimentalMaterialApi::class)
@MainNavGraph
@Destination
@Composable
fun AddBlogScreen(
    addBlogVM: AddBlogViewModel = hiltViewModel(),
    addBlogState: SelectImageScreenState = rememberSelectImageScreenState(),
    rootDestinations: ActionRootDestinations
) {
    BackHandler(addBlogState.isShowModal) {
        addBlogState.hiddenModal()
    }
    ModalBottomSheetLayout(
        sheetState = addBlogState.modalBottomSheetState,
        sheetContent = {
            SelectImgButtonSheet(
                actionHidden = addBlogState::hiddenModal,
                isVisible = addBlogState.isShowModal
            ) { uri ->
                addBlogState.hiddenModal()
                uri?.let { addBlogVM.imageBlog.changeValue(it) }
            }
        },
    ) {
        Scaffold(
            topBar = {
                ToolbarBack(
                    stringResource(R.string.title_toolbar_add_blog),
                    actionBack = rootDestinations::backDestination
                )
            },
            floatingActionButton = {
                ButtonPublish(isEnable = addBlogVM.isValidData) {
                    if (addBlogVM.getPostValidate(addBlogState.context)) {
                        rootDestinations.backDestination()
                    }
                }
            },
            floatingActionButtonPosition = FabPosition.Center
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
            ) {
                ImageNewBlog(
                    imgBlog = addBlogVM.imageBlog.value,
                    isCompress = addBlogVM.imageBlog.isCompress,
                    actionEditImg = {
                        addBlogState.hiddenKeyBoard()
                        addBlogState.showModal()
                    }
                )
                EditableTextSavable(
                    valueProperty = addBlogVM.description,
                    modifier = Modifier
                        .padding(20.dp),
                    modifierText = Modifier.height(150.dp)
                )
            }
        }
    }
}


@Composable
private fun ButtonPublish(
    isEnable: Boolean,
    actionValidate: () -> Unit,
) {
    Button(onClick = actionValidate, enabled = isEnable) {
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
    imgBlog: Uri,
    isCompress: Boolean,
    actionEditImg: () -> Unit,
) {
    Card(modifier = Modifier.padding(10.dp)) {
        Box(contentAlignment = Alignment.Center) {
            SubcomposeAsyncImage(
                model = imgBlog,
                contentDescription = stringResource(id = R.string.description_img_loaded),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            ) {

                when {
                    painter.state is AsyncImagePainter.State.Success -> SubcomposeAsyncImageContent()
                    painter.state is AsyncImagePainter.State.Error && imgBlog != Uri.EMPTY -> Icon(
                        painter = painterResource(id = R.drawable.ic_broken_image),
                        contentDescription = stringResource(id = R.string.description_error_load_img),
                        modifier = Modifier.padding(40.dp)
                    )
                    else -> Icon(
                        painter = painterResource(id = R.drawable.ic_image),
                        contentDescription = stringResource(id = R.string.description_img_loaded),
                        modifier = Modifier.padding(40.dp)
                    )
                }
            }
            if (isCompress)
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
