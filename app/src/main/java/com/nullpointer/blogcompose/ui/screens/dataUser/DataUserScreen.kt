package com.nullpointer.blogcompose.ui.screens.dataUser

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter
import coil.request.CachePolicy
import com.nullpointer.blogcompose.R
import com.nullpointer.blogcompose.core.states.Resource
import com.nullpointer.blogcompose.presentation.AuthViewModel
import com.nullpointer.blogcompose.ui.customs.ToolbarBack
import com.nullpointer.blogcompose.ui.screens.addPost.components.ButtonSheetContent
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DataUserScreen(
    authViewModel: AuthViewModel = hiltViewModel(),
) {
    // * reload info user
    authViewModel.reLoadInfoUser()
    val statusChange = authViewModel.stateUpdateUser.collectAsState()
    val scaffoldState = rememberScaffoldState()
    val sheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(key1 = Unit) {
        authViewModel.messageAuth.collect {
            scaffoldState.snackbarHostState.showSnackbar(message = it,
                duration = SnackbarDuration.Short)
        }
    }

    ModalBottomSheetLayout(sheetContent = {
        ButtonSheetContent {
            authViewModel.changeImgFileTemp(it, context)
            scope.launch {
                sheetState.hide()
            }

        }
    }, sheetState = sheetState) {
        Scaffold(
            scaffoldState = scaffoldState,
            topBar = { ToolbarBack(title = "Información de perfil") },
            floatingActionButton = {
                ButtonRegistryStatus(
                    statusChange.value) {
                    authViewModel.updateDataUser(context)
                }
            }
        ) {
            Column {
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .weight(5f)) {
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(.5f)
                        .background(MaterialTheme.colors.primary))
                    ImageCirculateUser(fileTemp = authViewModel.fileImg,
                        imageProfile = authViewModel.photoUser,
                        modifier = Modifier.align(Alignment.Center),
                        isCompress = authViewModel.isCompress.value) {
                        scope.launch {
                            sheetState.show()
                        }
                    }
                }

                Box(modifier = Modifier
                    .fillMaxWidth()
                    .weight(4f)
                ) {
                    TextInputName(
                        nameUser = authViewModel.nameUser,
                        changeNameUser = authViewModel::changeNameUserTemp,
                        errorMessage = authViewModel.errorName,
                        maxLength = AuthViewModel.MAX_LENGTH_NAME_USER,
                        modifier = Modifier.align(Alignment.Center))
                }
                Spacer(modifier = Modifier.weight(2f))

            }
        }
    }

}

@Composable
fun ImageCirculateUser(
    fileTemp: File?,
    imageProfile: String,
    modifier: Modifier = Modifier,
    isCompress: Boolean,
    actionEdit: () -> Unit,
) {
    val painter = rememberImagePainter(data = when {
        fileTemp != null -> fileTemp
        imageProfile.isNotEmpty() -> imageProfile
        else -> R.drawable.ic_person
    }) {
        crossfade(true)

    }
    val state = painter.state
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Card(shape = CircleShape, elevation = 10.dp) {
            Image(
                painter = when (state) {
                    is ImagePainter.State.Error -> painterResource(id = R.drawable.ic_broken_image)
                    else -> painter
                },
                contentScale = ContentScale.Crop,
                contentDescription = "",
                modifier = Modifier
                    .size(150.dp)
                    .padding(if (state !is ImagePainter.State.Success) 30.dp else 0.dp)
            )
        }
        if (state is ImagePainter.State.Loading || isCompress) {
            CircularProgressIndicator()
        }
        FloatingActionButton(onClick = { actionEdit() }, modifier = Modifier
            .padding(10.dp)
            .size(35.dp)
            .align(Alignment.BottomEnd)
        ) {
            Icon(
                painterResource(id = R.drawable.ic_edit),
                "",
            )
        }
    }


}

@Composable
fun TextInputName(
    modifier: Modifier = Modifier,
    nameUser: String,
    changeNameUser: (String) -> Unit,
    errorMessage: Int,
    maxLength: Int,
) {
    Column(modifier = modifier
        .fillMaxWidth(.8f)
    ) {
        OutlinedTextField(
            value = nameUser,
            singleLine = true,
            onValueChange = changeNameUser,
            label = { Text("Nombre de usuario") },
            isError = errorMessage != 0
        )
        if (errorMessage != 0) {
            Text(stringResource(id = errorMessage),
                style = MaterialTheme.typography.caption,
                color = MaterialTheme.colors.error,
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(end = 20.dp))
        } else {
            Text("${nameUser.length}/$maxLength",
                style = MaterialTheme.typography.caption,
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(end = 10.dp))
        }
    }

}

@Composable
fun ButtonRegistryStatus(

    statusChange: Resource<Unit>?, actionClick: () -> Unit,
) {
    FloatingActionButton(onClick = { if (statusChange == null) actionClick() },
        modifier = Modifier
            .animateContentSize(), backgroundColor = when (statusChange) {
            is Resource.Failure -> Color(0xffcc0000)
            is Resource.Loading -> Color.LightGray
            is Resource.Success -> Color(0xff00cc00)
            null -> MaterialTheme.colors.secondary
        }) {
        when (statusChange) {
            is Resource.Failure -> Icon(
                painter = painterResource(id = R.drawable.ic_clear),
                contentDescription = "")
            is Resource.Loading -> CircularProgressIndicator()
            is Resource.Success -> Icon(
                painter = painterResource(id = R.drawable.ic_ckeck),
                contentDescription = "")
            null -> Text("Registrarse", modifier = Modifier.padding(horizontal = 20.dp))
        }
    }
}