package com.nullpointer.blogcompose.ui.screens.profileScreen

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.nullpointer.blogcompose.R
import com.nullpointer.blogcompose.models.users.SimpleUser

@Composable
fun HeaderUser(
    user: SimpleUser,
    actionEditPhoto: () -> Unit,
    actionSettings: () -> Unit
) {
    Card {
        Box {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                PhotoProfile(
                    urlImage = user.urlImg,
                    clickEditPhoto = actionEditPhoto
                )
                Spacer(modifier = Modifier.height(15.dp))
                Text(text = user.name)
            }
            IconButtonSettings(
                modifier = Modifier.align(Alignment.TopEnd),
                actionClickSettings = actionSettings
            )
        }
    }
}

@Composable
private fun PhotoProfile(
    modifier: Modifier = Modifier,
    urlImage: String,
    clickEditPhoto: (() -> Unit)? = null
) {
    Box(modifier = modifier) {
        AsyncImage(
            model = ImageRequest
                .Builder(LocalContext.current)
                .data(urlImage)
                .transformations(CircleCropTransformation())
                .crossfade(true)
                .build(),
            modifier = Modifier.size(150.dp),
            contentDescription = stringResource(id = R.string.description_img_user),
            contentScale = ContentScale.Crop
        )
        FloatingActionButton(onClick = {
            clickEditPhoto?.invoke()
        }, modifier = Modifier
            .padding(10.dp)
            .size(35.dp)
            .align(Alignment.BottomEnd)) {
            Icon(
                painter = painterResource(id = R.drawable.ic_edit),
                contentDescription = stringResource(
                    id = R.string.description_edit_img_user
                )
            )
        }
    }

}

@Composable
private fun IconButtonSettings(
    modifier: Modifier = Modifier,
    actionClickSettings: (() -> Unit)? = null
) {
    IconButton(
        onClick = { actionClickSettings?.invoke() },
        modifier = modifier
            .padding(10.dp)
            .size(40.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_settings),
            contentDescription = stringResource(id = R.string.description_settings)
        )
    }
}