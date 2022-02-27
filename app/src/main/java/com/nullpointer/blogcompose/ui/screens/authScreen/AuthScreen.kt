package com.nullpointer.blogcompose.ui.screens.authScreen

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.nullpointer.blogcompose.R
import com.nullpointer.blogcompose.core.states.LoginStatus
import com.nullpointer.blogcompose.presentation.AuthViewModel
import timber.log.Timber

@Composable
fun AuthScreen(
    authViewModel: AuthViewModel = hiltViewModel(),
) {
    val scaffoldState = rememberScaffoldState()
    val stateAuth = authViewModel.stateAuth.collectAsState()
    val messageError=authViewModel.messageAuth.collectAsState(null)

    LaunchedEffect(messageError.value){
        messageError.value?.let{
            scaffoldState.snackbarHostState.showSnackbar(it)
        }
    }

    Scaffold(
        scaffoldState = scaffoldState,
        backgroundColor = MaterialTheme.colors.primary) {
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
            ContainerLogo()

            when (stateAuth.value) {
                LoginStatus.Authenticated -> Text("Usuario autenticado")
                LoginStatus.Authenticating -> Text("Autenticando...")
                LoginStatus.Unauthenticated -> Text("Usuario no autenticado")
            }



            Column(modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.4f)
                .background(MaterialTheme.colors.error),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ButtonLoginGoogle(
                    authWithTokeGoogle = authViewModel::authWithTokeGoogle,
                    logoOut = authViewModel::logOut,
                    loginStatus = stateAuth.value,
                )
                ExtendedFloatingActionButton(onClick = { /*TODO*/ },
                    text = { Text("Ingresa con Facebook") },
                    icon = {
                        Icon(painterResource(id = R.drawable.ic_facebook),
                            contentDescription = "",
                            modifier = Modifier.size(25.dp), tint = Color.Unspecified)
                    },
                    modifier = Modifier.width(250.dp),
                    backgroundColor = Color.White
                )
            }


        }
    }
}

@Composable
fun ButtonLoginGoogle(
    authWithTokeGoogle: (String) -> Unit,
    logoOut: () -> Unit,
    loginStatus: LoginStatus,
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
            val account = task.getResult(ApiException::class.java)!!
            authWithTokeGoogle(account.idToken!!)
        } catch (e: ApiException) {
            Timber.d("Google sign in failed $e")
        }
    }


    FloatingActionButton(
        onClick = {
            when (loginStatus) {
                LoginStatus.Authenticated -> {
                    googleSignInClient.signOut()
                    logoOut()
                }
                LoginStatus.Authenticating -> Unit
                LoginStatus.Unauthenticated -> launcher.launch(googleSignInClient.signInIntent)
            }
        },
        modifier = Modifier.animateContentSize(),
        backgroundColor = Color.White
    ) {
        Row(verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.padding(horizontal = 25.dp)
        ) {
            when (loginStatus) {
                LoginStatus.Authenticated -> {
                    Icon(painterResource(id = R.drawable.ic_google),
                        contentDescription = "",
                        modifier = Modifier.size(24.dp), tint = Color.Unspecified)
                    Spacer(modifier = Modifier.width(15.dp))
                    Text("Cerrar session")
                }
                LoginStatus.Authenticating -> {
                    CircularProgressIndicator(modifier = Modifier.size(30.dp))
                    Spacer(modifier = Modifier.width(15.dp))
                    Text("Autenticando ...")
                }
                LoginStatus.Unauthenticated -> {
                    Icon(painterResource(id = R.drawable.ic_google),
                        contentDescription = "",
                        modifier = Modifier.size(24.dp), tint = Color.Unspecified)
                    Spacer(modifier = Modifier.width(15.dp))
                    Text("Ingresa con Google")
                }
            }
        }

    }
}

@Composable
fun ContainerLogo() {
    Box(modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight(0.3f)
        .background(MaterialTheme.colors.error),
        contentAlignment = Alignment.Center) {
        Box(modifier = Modifier
            .size(100.dp)
            .background(Color.White)) {

        }
    }
}
