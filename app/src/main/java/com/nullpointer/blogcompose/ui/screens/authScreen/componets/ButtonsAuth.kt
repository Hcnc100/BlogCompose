package com.nullpointer.blogcompose.ui.screens.authScreen.componets

import android.content.Context
import android.widget.Toast
import androidx.activity.ComponentActivity
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
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.GoogleAuthProvider
import com.nullpointer.blogcompose.R
import timber.log.Timber
import java.util.*

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
    actionBeforeAuth: (AuthCredential) -> Unit,
) {
    val context = LocalContext.current
    val googleSignInClient = getGoogleSignInClient(context)
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
        try {
            val exception = task.exception
            if (exception != null) throw exception
            val account = task.getResult(ApiException::class.java)!!
            actionBeforeAuth(GoogleAuthProvider.getCredential(account.idToken, null))
        } catch (e: ApiException) {
            Toast.makeText(context, "Error al ingresar", Toast.LENGTH_SHORT).show()
            Timber.d("Google sign in failed $e")
        }
    }

    ExtendedFloatingActionButton(
        modifier = modifier,
        onClick = { launcher.launch(googleSignInClient.signInIntent) },
        backgroundColor = Color.White,
        text = { Text("Ingresa con Google") },
        icon = {
            Icon(painterResource(id = R.drawable.ic_google),
                contentDescription = "",
                modifier = Modifier.size(24.dp), tint = Color.Unspecified)
        }
    )
}

fun authWithFacebook(
    activity: ComponentActivity,
    actionBeforeAuth: (AuthCredential) -> Unit,
) {
    val callbackManager: CallbackManager = CallbackManager.Factory.create()
    val callback = object : FacebookCallback<LoginResult> {
        override fun onCancel() {
            Timber.d("Cancel login facebook ")
        }

        override fun onError(error: FacebookException) {
            Toast.makeText(activity, "Error al ingresar", Toast.LENGTH_SHORT).show()
            Timber.e("Error login facebook $error")
        }

        override fun onSuccess(result: LoginResult) {
            Timber.d("Sucees login facebook")
            actionBeforeAuth(FacebookAuthProvider.getCredential(result.accessToken.token))
        }
    }
    LoginManager.getInstance().registerCallback(callbackManager, callback)
    LoginManager.getInstance().logInWithReadPermissions(
        activity, callbackManager, listOf("public_profile", "email")
    )
}

@Composable
fun ButtonAuthFacebook(
    modifier: Modifier = Modifier,
    actionBeforeAuth: (AuthCredential) -> Unit,
) {
    val activity = LocalContext.current as ComponentActivity
    ExtendedFloatingActionButton(
        modifier = modifier,
        onClick = { authWithFacebook(activity, actionBeforeAuth) },
        text = { Text("Ingresa con Facebook") },
        icon = {
            Icon(painterResource(id = R.drawable.ic_facebook),
                contentDescription = "",
                modifier = Modifier.size(25.dp), tint = Color.Unspecified)
        },
        backgroundColor = Color.White
    )
}