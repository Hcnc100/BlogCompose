package com.nullpointer.blogcompose.ui.screens.addPost

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.annotation.ExperimentalCoilApi
import com.nullpointer.blogcompose.R
import com.nullpointer.blogcompose.services.uploadImg.UploadPostServices
import com.nullpointer.blogcompose.ui.share.ToolbarBack
import com.nullpointer.blogcompose.ui.share.ButtonSheetContent
import com.nullpointer.blogcompose.ui.screens.addPost.viewModel.AddBlogViewModel
import com.nullpointer.blogcompose.ui.share.BackHandler
import com.nullpointer.blogcompose.ui.share.ImagePost
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterialApi::class)
@Destination
@Composable
fun AddBlogScreen(
    addBlogVM: AddBlogViewModel = hiltViewModel(),
    navigator: DestinationsNavigator,
) {
    val scope = rememberCoroutineScope()
    val modalState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    // * state for animation button add post
    val (buttonVisible, changeButtonVisible) = rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(key1 = Unit) {
        delay(300)
        changeButtonVisible(true)
    }
    BackHandler(buttonVisible) {
        changeButtonVisible(false)
        navigator.popBackStack()
    }

    ModalBottomSheetLayout(sheetState = modalState,
        sheetContent = {
            if (modalState.isVisible) {
                // * bottom sheet really
                ButtonSheetContent(
                    scope = scope,
                    sheetState = modalState,
                    actionBeforeSelect = { uri ->
                        scope.launch { modalState.hide() }
                        uri?.let { addBlogVM.changeFileImg(it, context) }
                    }
                )
            } else {
                // * fake bottom sheet
                //  ! is important for consistency animations
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp))
            }
        },
        // * fake color sheet
        //  ! is important for consistency animations
        sheetBackgroundColor = if (modalState.isVisible) MaterialTheme.colors.surface else Color.Transparent) {
        Scaffold(
            topBar = {
                ToolbarBack(stringResource(R.string.title_toolbar_add_blog)) {
                    changeButtonVisible(false)
                    navigator.popBackStack()
                }
            },
            floatingActionButton = {
                // * show button only finish animation
                if (buttonVisible) {
                    ButtonPublish {
                        if (addBlogVM.validate()) {
                            UploadPostServices.startServicesUploadPost(
                                context = context,
                                description = addBlogVM.description,
                                fileImage = addBlogVM.fileImg!!
                            )
                            Toast.makeText(context,
                                context.getString(R.string.text_message_upload_post),
                                Toast.LENGTH_SHORT).show()
                            changeButtonVisible(false)
                            navigator.popBackStack()
                        }
                    }
                }
            },
        ) {
            Column(modifier = Modifier
                .verticalScroll(rememberScrollState())) {
                ImageNewBlog(
                    fileImg = addBlogVM.fileImg,
                    isCompress = addBlogVM.isCompress.value,
                    errorImage = addBlogVM.errorImage,
                    // * clear focus when launch bottom sheet
                    actionEditImg = {
                        focusManager.clearFocus()
                        scope.launch {
                            modalState.show()
                        }
                    })
                Spacer(modifier = Modifier.height(10.dp))
                DescriptionNewBlog(
                    descriptionBlog = addBlogVM.description,
                    changeDescriptionBlog = addBlogVM::changeDescription,
                    maxLengthDescription = AddBlogViewModel.MAX_LENGTH_DESCRIPTION,
                    errorDescription = addBlogVM.errorDescription,
                )
            }
        }
    }
}


@Composable
fun ButtonPublish(
    actionValidate: () -> Unit,
) {
    ExtendedFloatingActionButton(
        icon = { Icon(painterResource(id = R.drawable.ic_publish), stringResource(R.string.description_icon_upload)) },
        text = { Text(stringResource(R.string.text_message_public_post)) },
        onClick = actionValidate,
    )
}

@Composable
fun DescriptionNewBlog(
    descriptionBlog: String,
    changeDescriptionBlog: (String) -> Unit,
    maxLengthDescription: Int,
    errorDescription: Int,
) {
    Column(modifier = Modifier
        .padding(10.dp)
        .fillMaxWidth()) {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            value = descriptionBlog,
            onValueChange = changeDescriptionBlog,
            label = { Text(stringResource(R.string.text_label_description_post)) },
            placeholder = { Text(stringResource(R.string.text_place_holder_description)) },
            shape = RoundedCornerShape(20.dp),
            isError = errorDescription != 0
        )
        // * show counter character or show message error
        Text(modifier = Modifier
            .align(Alignment.End)
            .padding(horizontal = 10.dp, vertical = 5.dp),
            text = if (errorDescription != 0) stringResource(id = errorDescription) else "${descriptionBlog.length}/$maxLengthDescription",
            style = MaterialTheme.typography.caption,
            color = if (errorDescription != 0) MaterialTheme.colors.error else MaterialTheme.colors.onBackground
        )
    }

}

@OptIn(ExperimentalCoilApi::class)
@Composable
fun ImageNewBlog(
    fileImg: File?,
    isCompress: Boolean,
    errorImage: Int,
    actionEditImg: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally) {
        Card(shape = RoundedCornerShape(10.dp),
            modifier = Modifier.padding(10.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                // * image selected
                ImagePost(
                    fileImg = fileImg,
                    paddingLoading = 70.dp,
                    showProgress = true,
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .aspectRatio(1f)
                        .background(when {
                            fileImg != null -> Color.Transparent
                            isSystemInDarkTheme() -> Color.DarkGray
                            else -> Color.LightGray
                        }),
                    contentDescription = stringResource(R.string.description_img_new_post)
                )
                // * progress indicate compress
                if (isCompress) CircularProgressIndicator()
                // * button edit image select
                FloatingActionButton(
                    onClick = { actionEditImg() },
                    modifier = Modifier
                        .padding(10.dp)
                        .size(40.dp)
                        .align(Alignment.BottomEnd)
                ) {
                    Icon(painterResource(id = R.drawable.ic_edit), stringResource(R.string.description_edit_img_post))
                }
            }
        }
        // * error message image
        if (errorImage != 0) {
            Text(text = stringResource(id = errorImage),
                style = MaterialTheme.typography.caption,
                color = MaterialTheme.colors.error)
        }
    }

}
