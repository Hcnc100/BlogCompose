package com.nullpointer.blogcompose.ui.screens.config

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import com.nullpointer.blogcompose.R
import com.nullpointer.blogcompose.presentation.AuthViewModel
import com.nullpointer.blogcompose.ui.customs.ToolbarBack
import com.nullpointer.blogcompose.ui.screens.authScreen.getGoogleSignInClient
import com.nullpointer.blogcompose.ui.screens.destinations.ConfigScreenDestination
import com.nullpointer.blogcompose.ui.screens.destinations.DataUserScreenDestination
import com.nullpointer.blogcompose.ui.share.ImageProfile
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Composable
@Destination
fun ConfigScreen(
    authViewModel: AuthViewModel,
    navigator: DestinationsNavigator,
) {
    val currentUser = authViewModel.currentUser.collectAsState().value
    val context = LocalContext.current
    Scaffold(
        topBar = { ToolbarBack(title = "Configuración", navigator::popBackStack) }
    ) {
        Column(verticalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxSize()) {
            Column {

                Card(modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.padding(10.dp)
                    ) {
                        Text(text = currentUser?.nameUser ?: "", overflow = TextOverflow.Ellipsis)

                        ImageProfile(urlImgProfile = currentUser?.urlImg.toString(),
                            paddingLoading = 5.dp,
                            sizeImage = 35.dp)
                    }
                }

                Card(modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .clickable {
                        navigator.navigate(DataUserScreenDestination)
                    }) {
                    Text(text = "Cambiar nombre o foto", modifier = Modifier.padding(15.dp))
                }

            }

            Card(modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .clickable {
                    // ! logout with google account
                    getGoogleSignInClient(context).signOut()
                    // ! logout with firebase
                    authViewModel.logOut()
                }) {
                Row(modifier = Modifier
                    .padding(15.dp)
                    .clickable {
                        // ! this is need for no crash for with restore state navigate
                        navigator.popBackStack()
                        authViewModel.logOut()
                    }) {
                    Image(painter = painterResource(id = R.drawable.ic_logout),
                        contentDescription = "")
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = "Cerrar session")
                }

            }
        }

    }
}