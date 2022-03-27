package com.nullpointer.blogcompose.ui.screens.authScreen.componets.facebook

import android.widget.Toast
import androidx.activity.ComponentActivity
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
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FacebookAuthProvider
import com.nullpointer.blogcompose.R
import timber.log.Timber

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