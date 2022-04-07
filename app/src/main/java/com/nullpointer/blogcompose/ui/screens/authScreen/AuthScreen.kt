package com.nullpointer.blogcompose.ui.screens.authScreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.google.firebase.auth.AuthCredential
import com.nullpointer.blogcompose.R
import com.nullpointer.blogcompose.core.states.Resource
import com.nullpointer.blogcompose.presentation.AuthViewModel
import com.nullpointer.blogcompose.ui.screens.authScreen.componets.google.ButtonAuthGoogle
import com.nullpointer.blogcompose.ui.screens.authScreen.componets.facebook.ButtonAuthFacebook
import com.ramcosta.composedestinations.annotation.Destination

@Composable
@Destination(start = true)
fun AuthScreen(
    authViewModel: AuthViewModel,
) {
    // * states
    val scaffoldState = rememberScaffoldState()
    val authStatus = authViewModel.stateAuthentication.collectAsState()

    // * messages auth
    val messageError = authViewModel.messageAuth
    val context= LocalContext.current
    LaunchedEffect(messageError) {
        messageError.collect {
            scaffoldState.snackbarHostState.showSnackbar(
                context.getString(it)
            )
        }
    }

    Scaffold(
        scaffoldState = scaffoldState,
        backgroundColor = MaterialTheme.colors.primary) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // * container logo app
            ContainerLogo()
            // * container buttons login
            // ! hide while authenticated
            Box(modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.6f)) {

                ButtonsAuth(
                    authStatus.value !is Resource.Loading,
                    modifier = Modifier.fillMaxSize(),
                    authWithCredential = authViewModel::authWithCredential
                )
                ProgressAuth(isVisible = authStatus.value is Resource.Loading)
            }
        }
    }
}

@Composable
fun ContainerLogo() {
    Box(modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight(0.3f),
        contentAlignment = Alignment.Center) {
        Card(
            shape = CircleShape,
            elevation = 10.dp
        ) {
            Image(painter = rememberImagePainter(data = R.mipmap.ic_launcher),
                contentDescription = stringResource(R.string.description_logo_app),
                modifier = Modifier
                    .size(150.dp)
                    .padding(3.dp))
        }
    }
}


@Composable
fun ProgressAuth(
    isVisible: Boolean,
) {
    AnimatedVisibility(isVisible,
        enter = fadeIn(),
        exit = fadeOut()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = MaterialTheme.colors.error)
        }
    }
}

@Composable
fun ButtonsAuth(
    showButtonsAuth: Boolean,
    modifier: Modifier = Modifier,
    authWithCredential: (AuthCredential) -> Unit,
) {
    AnimatedVisibility(visible = showButtonsAuth,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Column(modifier = modifier,
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // * text disclaimer
            Text(stringResource(R.string.text_disclaimer),
                style = MaterialTheme.typography.caption,
                textAlign = TextAlign.Center,
                color = Color.White,
                modifier = Modifier
                    .width(120.dp)
                    .align(Alignment.CenterHorizontally))
            // * button auth with google
            ButtonAuthGoogle(
                modifier = Modifier.width(250.dp),
                actionBeforeAuth = authWithCredential,
            )
            // * button auth with facebook
            ButtonAuthFacebook(
                modifier = Modifier.width(250.dp),
                actionBeforeAuth = authWithCredential,
            )
        }
    }

}
