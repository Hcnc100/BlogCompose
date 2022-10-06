package com.nullpointer.blogcompose.ui.screens.authScreen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.google.firebase.auth.AuthCredential
import com.nullpointer.blogcompose.R
import com.nullpointer.blogcompose.presentation.AuthViewModel
import com.nullpointer.blogcompose.ui.navigation.MainNavGraph
import com.nullpointer.blogcompose.ui.screens.authScreen.componets.facebook.ButtonAuthFacebook
import com.nullpointer.blogcompose.ui.screens.authScreen.componets.google.ButtonAuthGoogle
import com.nullpointer.blogcompose.ui.screens.states.SimpleScreenState
import com.nullpointer.blogcompose.ui.screens.states.rememberSimpleScreenState
import com.ramcosta.composedestinations.annotation.Destination

@MainNavGraph(start = true)
@Destination
@Composable
fun AuthScreen(
    authViewModel: AuthViewModel,
    authScreenState: SimpleScreenState = rememberSimpleScreenState()
) {
    LaunchedEffect(key1 = Unit) {
        authViewModel.messageAuth.collect(authScreenState::showSnackMessage)
    }

    AuthScreen(
        isAuthenticated = authViewModel.isProcessing,
        scaffoldState = authScreenState.scaffoldState,
        actionAuthWithCredential = authViewModel::authWithCredential
    )

}

@Composable
fun AuthScreen(
    isAuthenticated: Boolean,
    scaffoldState: ScaffoldState,
    actionAuthWithCredential: (AuthCredential) -> Unit
) {
    Scaffold(
        scaffoldState = scaffoldState,
        backgroundColor = MaterialTheme.colors.primary
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            ContainerLogo()
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                contentAlignment = Alignment.Center
            ) {
                if (isAuthenticated) {
                    CircularProgressIndicator(color = MaterialTheme.colors.onPrimary)
                } else {
                    ButtonsAuth(authWithCredential = actionAuthWithCredential)
                }
            }
        }
    }
}


@Composable
private fun ContainerLogo(
    modifier: Modifier = Modifier
) {
    AsyncImage(
        model = R.mipmap.ic_launcher,
        contentDescription = stringResource(R.string.description_logo_app),
        modifier = modifier
            .fillMaxWidth()
            .height(250.dp)
            .wrapContentSize(Alignment.Center)
            .size(150.dp)
            .clip(CircleShape),
    )
}




@Composable
private fun ButtonsAuth(
    modifier: Modifier = Modifier,
    authWithCredential: (AuthCredential) -> Unit,
) {

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // * text disclaimer
        Text(
            stringResource(R.string.text_disclaimer),
            style = MaterialTheme.typography.caption,
            textAlign = TextAlign.Center,
            color = Color.White,
            modifier = Modifier
                .width(120.dp)
                .align(Alignment.CenterHorizontally)
        )
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


