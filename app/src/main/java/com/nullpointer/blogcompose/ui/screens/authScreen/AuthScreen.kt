package com.nullpointer.blogcompose.ui.screens.authScreen

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.nullpointer.blogcompose.R
import com.nullpointer.blogcompose.core.states.Resource
import com.nullpointer.blogcompose.presentation.AuthViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import timber.log.Timber

@Composable
@Destination
fun AuthScreen(
    authViewModel: AuthViewModel,
    navigator: DestinationsNavigator,
) {
    val scaffoldState = rememberScaffoldState()
    val authStatus = authViewModel.stateAuthentication.collectAsState()
    val messageError = authViewModel.messageAuth.collectAsState(null)

    LaunchedEffect(messageError.value) {
        messageError.value?.let {
            scaffoldState.snackbarHostState.showSnackbar(it)
        }
    }

    Scaffold(
        scaffoldState = scaffoldState,
        backgroundColor = MaterialTheme.colors.primary) {
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {

            ContainerLogo()


            // * container buttons login
            // ! hide while authenticated
            Box(modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.6f)) {
                androidx.compose.animation.AnimatedVisibility(visible = authStatus.value !is Resource.Loading,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    ButtonsAuth(modifier = Modifier.fillMaxSize(),
                        authWithTokeGoogle = authViewModel::authWithTokeGoogle)
                }

                androidx.compose.animation.AnimatedVisibility(visible = authStatus.value is Resource.Loading,
                    enter = fadeIn(),
                    exit = fadeOut()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MaterialTheme.colors.error)
                    }
                }
            }


        }
    }
}

@Composable
fun ButtonsAuth(
    modifier: Modifier = Modifier,
    authWithTokeGoogle: (String) -> Unit,
) {
    Column(modifier = modifier,
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text("Al hacer click en iniciar session aceptas los terminos y condiciones",
            style = MaterialTheme.typography.caption,
            textAlign = TextAlign.Center,
            color = Color.White,
            modifier = Modifier
                .width(120.dp)
                .align(Alignment.CenterHorizontally))

        ButtonLoginGoogle(
            modifier = Modifier.width(250.dp),
            authWithTokeGoogle = authWithTokeGoogle,
        )
        ExtendedFloatingActionButton(
            modifier = Modifier.width(250.dp),
            onClick = { /*TODO*/ },
            text = { Text("Ingresa con Facebook") },
            icon = {
                Icon(painterResource(id = R.drawable.ic_facebook),
                    contentDescription = "",
                    modifier = Modifier.size(25.dp), tint = Color.Unspecified)
            },
            backgroundColor = Color.White
        )
    }
}


@Composable
fun ButtonLoginGoogle(
    modifier: Modifier = Modifier,
    authWithTokeGoogle: (String) -> Unit,
) {
    val context = LocalContext.current
    val token = stringResource(R.string.default_web_client_id)
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(token)
        .requestEmail()
        .build()
    val googleSignInClient = GoogleSignIn.getClient(context, gso)
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
        try {
            val exception=task.exception
            if(exception!=null) throw exception
            val account = task.getResult(ApiException::class.java)!!
            authWithTokeGoogle(account.idToken!!)
        } catch (e: ApiException) {
            Timber.d("Google sign in failed $e")
        }
    }


    ExtendedFloatingActionButton(
        modifier = modifier,
        onClick = { launcher.launch(googleSignInClient.signInIntent) },
        backgroundColor = Color.White,
        text = {
            Text("Ingresa con Google")
        },
        icon = {
            Icon(painterResource(id = R.drawable.ic_google),
                contentDescription = "",
                modifier = Modifier.size(24.dp), tint = Color.Unspecified)
        }
    )
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
            val painter = rememberImagePainter(data = R.mipmap.ic_launcher)
            Image(painter = painter, "",
                Modifier
                    .size(150.dp)
                    .padding(3.dp))
        }
    }
}
