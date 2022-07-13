package com.nullpointer.blogcompose.ui.screens.config

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.nullpointer.blogcompose.R
import com.nullpointer.blogcompose.presentation.AuthViewModel
import com.nullpointer.blogcompose.ui.interfaces.ActionRootDestinations
import com.nullpointer.blogcompose.ui.navigation.RootNavGraph
import com.nullpointer.blogcompose.ui.share.ImageProfile
import com.nullpointer.blogcompose.ui.share.ToolbarBack
import com.ramcosta.composedestinations.annotation.Destination

@RootNavGraph
@Composable
@Destination
fun ConfigScreen(
    authViewModel: AuthViewModel,
    actionRootDestinations: ActionRootDestinations
) {
    val currentUser by authViewModel.currentUser.collectAsState()
    Scaffold(
        topBar = {
            ToolbarBack(
                title = stringResource(R.string.title_config),
                actionRootDestinations::backDestination
            )
        }
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxSize()
        ) {
            // * main buttons
            Column {
                ButtonCard(text = currentUser.name) {
                    ImageProfile(
                        urlImgProfile = currentUser.urlImg,
                        paddingLoading = 5.dp,
                        modifier = Modifier.size(30.dp),
                        contentDescription = stringResource(R.string.description_image_profile)
                    )
                }
            }
            // * button log out
            ButtonCard(
                text = stringResource(R.string.close_session),
                actionClick = authViewModel::logOut
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_logout),
                    contentDescription = stringResource(R.string.description_close_session)
                )
            }
        }

    }
}

@Composable
private fun ButtonCard(
    text: String,
    actionClick: (() -> Unit)? = null,
    iconButton: (@Composable () -> Unit)? = null,
) {
    Card(modifier = Modifier
        .fillMaxWidth()
        .padding(10.dp)
        .clickable { actionClick?.invoke() }) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 12.dp)
        ) {
            Text(text = text, overflow = TextOverflow.Ellipsis)
            iconButton?.invoke()
        }
    }
}