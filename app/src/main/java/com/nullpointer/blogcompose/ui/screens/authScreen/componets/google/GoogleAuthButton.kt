package com.nullpointer.blogcompose.ui.screens.authScreen.componets.google

import android.content.Context
import android.widget.Toast
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.AuthCredential
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
            Toast.makeText(context,
                context.getString(R.string.text_error_login_google),
                Toast.LENGTH_SHORT).show()
            Timber.e("Google sign in failed $e")
        }
    }

    ExtendedFloatingActionButton(
        modifier = modifier,
        onClick = { launcher.launch(googleSignInClient.signInIntent) },
        backgroundColor = Color.White,
        text = { Text(stringResource(R.string.text_login_google)) },
        icon = {
            Icon(painterResource(id = R.drawable.ic_google),
                contentDescription = stringResource(R.string.description_icon_google),
                modifier = Modifier.size(24.dp), tint = Color.Unspecified)
        }
    )
}

