package com.nullpointer.blogcompose.ui.screens.config

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberImagePainter
import com.nullpointer.blogcompose.R
import com.nullpointer.blogcompose.presentation.AuthViewModel
import com.nullpointer.blogcompose.ui.customs.ToolbarBack

@Composable
fun ConfigScreen(
    authViewModel: AuthViewModel = hiltViewModel(),
) {
    Scaffold(
        topBar = { ToolbarBack(title = "Configuración") }
    ) {
        Column(verticalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxSize()) {
            Column {

                Card(modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)) {
                    Row {
//                        Text(text = authViewModel.nameUser, overflow = TextOverflow.Ellipsis)
//                        val painter = rememberImagePainter(data = authViewModel.photoUser)
//                        Image(painter = painter,
//                            contentDescription = "",
//                            modifier = Modifier.size(50.dp))
                    }
                }

                Card(modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)) {
                    Text(text = "Cambiar nombre o foto", modifier = Modifier.padding(15.dp))
                }

            }

            Card(modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)) {
                Row(modifier = Modifier
                    .padding(15.dp)
                    .clickable {
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