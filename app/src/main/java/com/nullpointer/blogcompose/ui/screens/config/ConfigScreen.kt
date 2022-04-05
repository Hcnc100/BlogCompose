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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.nullpointer.blogcompose.R
import com.nullpointer.blogcompose.presentation.AuthViewModel
import com.nullpointer.blogcompose.ui.customs.ToolbarBack
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
    Scaffold(
        topBar = { ToolbarBack(title = "Configuración", navigator::popBackStack) }
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxSize()
        ) {
            // * main buttons
            Column {
                ButtonCard(text = currentUser?.nameUser.toString()) {
                    ImageProfile(urlImgProfile = currentUser?.urlImg.toString(),
                        paddingLoading = 5.dp,
                        modifier = Modifier.size(30.dp)
                    )
                }
                ButtonCard(text = "Cambiar foto del usuario", actionClick = {
                    navigator.navigate(DataUserScreenDestination)
                })
            }
            // * button log out
            ButtonCard(text = "Cerrar session", actionClick = {
                navigator.popBackStack()
                authViewModel.logOut()
            }) {
                Image(painter = painterResource(id = R.drawable.ic_logout),
                    contentDescription = "")
            }
        }

    }
}

@Composable
fun ButtonCard(
    text: String,
    actionClick: (() -> Unit)? = null,
    iconButton: (@Composable () -> Unit)? = null,
) {
    Card(modifier = Modifier
        .fillMaxWidth()
        .padding(10.dp)
        .clickable { actionClick?.invoke() }) {
        Row(verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 12.dp)
        ) {
            Text(text = text, overflow = TextOverflow.Ellipsis)
            iconButton?.invoke()
        }
    }
}