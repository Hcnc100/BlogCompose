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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.annotation.ExperimentalCoilApi
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter
import com.nullpointer.blogcompose.R
import com.nullpointer.blogcompose.core.states.LoginStatus
import com.nullpointer.blogcompose.core.states.Resource
import com.nullpointer.blogcompose.presentation.AuthViewModel
import com.nullpointer.blogcompose.presentation.RegistryViewModel
import com.nullpointer.blogcompose.ui.customs.ToolbarBack
import com.nullpointer.blogcompose.ui.screens.addPost.components.ButtonSheetContent
import com.nullpointer.blogcompose.ui.share.ImageProfile
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterialApi::class)
@Destination
@Composable
fun DataUserScreen(
    registryViewModel: RegistryViewModel = hiltViewModel(),
    authViewModel: AuthViewModel,
    navigator: DestinationsNavigator,
) {
    // * reload info user
    val statusChange = registryViewModel.stateUpdateUser.collectAsState(null)
    val scaffoldState = rememberScaffoldState()
    val sheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val isDataComplete =
        authViewModel.stateAuthUser.collectAsState().value is LoginStatus.Authenticated.CompleteData
    authViewModel.currentUser.collectAsState()



    LaunchedEffect(key1 = Unit) {
        registryViewModel.registryMessage.collect {
            scaffoldState.snackbarHostState.showSnackbar(message = it,
                duration = SnackbarDuration.Short)
        }
    }

    ModalBottomSheetLayout(sheetContent = {
        ButtonSheetContent(
            scope = scope,
            sheetState = sheetState
        ) { uri ->
            uri?.let { registryViewModel.changeImgFileTemp(it, context) }
            scope.launch { sheetState.hide() }
        }
    }, sheetState = sheetState) {
        Scaffold(
            scaffoldState = scaffoldState,
            topBar = {
                if (isDataComplete) {
                    // * if data is complete , show data saved
                    ToolbarBack(title = "Información de perfil") { navigator.popBackStack() }
                } else {
                    // ? if data is no complete no show nothing
                    ToolbarBack(title = "Completa tu cuenta")
                }
            },
            floatingActionButton = {
                ButtonRegistryStatus(
                    statusChange.value,
                    isDataComplete) {
                    registryViewModel.updateDataUser(context)
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
                    ImageCirculateUser(fileTemp = registryViewModel.fileImg,
                        imageProfile = registryViewModel.photoUser,
                        modifier = Modifier.align(Alignment.Center),
                        isCompress = registryViewModel.isCompress.value) {
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
                        nameUser = registryViewModel.nameUser,
                        changeNameUser = registryViewModel::changeNameUserTemp,
                        errorMessage = registryViewModel.errorName,
                        maxLength = 150,
                        modifier = Modifier.align(Alignment.Center))
                }
                Spacer(modifier = Modifier.weight(2f))

            }
        }
    }

}

@OptIn(ExperimentalCoilApi::class)
@Composable
fun ImageCirculateUser(
    fileTemp: File?,
    imageProfile: String,
    modifier: Modifier = Modifier,
    isCompress: Boolean,
    actionEdit: () -> Unit,
) {


    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        ImageProfile(
            urlImgProfile = imageProfile,
            paddingLoading = 20.dp,
            sizeImage = 150.dp,
            showProgress = true,
            fileImg = fileTemp,
        )
        if (isCompress) CircularProgressIndicator()

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
    statusChange: Resource<Unit>?,
    isRegistry: Boolean,
    actionClick: () -> Unit,
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
            null -> {
                val text = if (isRegistry) "Actulizar" else "Registrarse"
                Text(text, modifier = Modifier.padding(horizontal = 20.dp))
            }
        }
    }
}