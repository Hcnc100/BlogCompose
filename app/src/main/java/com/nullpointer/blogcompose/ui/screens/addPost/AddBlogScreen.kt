package com.nullpointer.blogcompose.ui.screens.addPost

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.annotation.ExperimentalCoilApi
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter
import com.nullpointer.blogcompose.R
import com.nullpointer.blogcompose.models.Post
import com.nullpointer.blogcompose.services.UploadPostServices
import com.nullpointer.blogcompose.ui.customs.ToolbarBack
import com.nullpointer.blogcompose.ui.screens.addPost.components.ButtonSheetContent
import com.nullpointer.blogcompose.ui.screens.addPost.viewModel.AddBlogViewModel
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AddBlogScreen(
    addBlogVM: AddBlogViewModel = hiltViewModel(),
) {
    val scope = rememberCoroutineScope()
    val modalState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val context = LocalContext.current
    ModalBottomSheetLayout(sheetState = modalState, sheetContent = {
        ButtonSheetContent(actionBeforeSelect = {
            addBlogVM.changeFileImg(it, context)
            scope.launch {
                modalState.hide()
            }
        })
    }) {
        Scaffold(
            topBar = { ToolbarBack("Nuevo Post") },
            floatingActionButton = {
                ButtonPublish {
                    if (addBlogVM.validate()) {
                        UploadPostServices.startServicesUploadPost(context,
                            Post.createRandom().copy(
                                description = addBlogVM.description,
                            ), addBlogVM.fileImg!!)
                    } else {
                        Toast.makeText(context, "Verifique sus datos", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        ) {
            Column(modifier = Modifier
                .verticalScroll(rememberScrollState())) {
                ImageNewBlog(addBlogVM.fileImg, addBlogVM.isCompress.value, addBlogVM.errorImage) {
                    scope.launch {
                        modalState.show()
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                DescriptionNewBlog(addBlogVM.description,
                    addBlogVM::changeDescription,
                    AddBlogViewModel.MAX_LENGTH_DESCRIPTION,
                    addBlogVM.errorDescription)
            }
        }
    }


}



@Composable
fun ButtonPublish(
    actionValidate: () -> Unit,
) {
    val context = LocalContext.current
    ExtendedFloatingActionButton(icon = { Icon(painterResource(id = R.drawable.ic_publish), "") },
        text = { Text("Publicar") },
        onClick = { actionValidate() })
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
            label = { Text("Descirpcion") },
            placeholder = { Text("Descripcion del blog") },
            shape = RoundedCornerShape(20.dp),
            isError = errorDescription != 0
        )
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
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Card(shape = RoundedCornerShape(10.dp),
            modifier = Modifier.padding(10.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                val painter = rememberImagePainter(data = fileImg) {
                    crossfade(true)
                }
                val state = painter.state
                Image(painter = when {
                    fileImg == null -> painterResource(id = R.drawable.ic_image)
                    state is ImagePainter.State.Error -> painterResource(id = R.drawable.ic_broken_image)
                    else -> painter
                },
                    contentDescription = "",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth(0.95f)
                        .aspectRatio(1f)
                        .padding(if (state !is ImagePainter.State.Success) 60.dp else 0.dp))
                if (state is ImagePainter.State.Loading || isCompress) {
                    CircularProgressIndicator()
                }

                FloatingActionButton(
                    onClick = { actionEditImg() },
                    modifier = Modifier
                        .padding(10.dp)
                        .size(40.dp)
                        .align(Alignment.BottomEnd)
                ) {
                    Icon(painterResource(id = R.drawable.ic_edit), "")
                }
            }
        }
        if (errorImage != 0) {
            Text(text = stringResource(id = errorImage),
                style = MaterialTheme.typography.caption,
                color = MaterialTheme.colors.error)
        }
    }

}
