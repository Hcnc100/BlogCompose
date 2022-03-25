package com.nullpointer.blogcompose.ui.screens.authScreen.componets

import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.size
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.nullpointer.blogcompose.R
import timber.log.Timber

fun getGoogleSignInClient(
    context: Context,
): GoogleSignInClient {
    val token = context.getString(R.string.default_web_client_id)
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(token)
        .requestEmail()
        .build()
    return GoogleSignIn.getClient(context, gso)
}

@Composable
fun ButtonAuthGoogle(
    modifier: Modifier = Modifier,
    actionBeforeAuth: (token: String) -> Unit,
) {
    val googleSignInClient = getGoogleSignInClient(LocalContext.current)
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
        try {
            val exception = task.exception
            if (exception != null) throw exception
            val account = task.getResult(ApiException::class.java)!!
            actionBeforeAuth(account.idToken!!)
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
fun ButtonAuthFacebook(
    modifier: Modifier= Modifier
) {
    ExtendedFloatingActionButton(
        modifier = modifier,
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