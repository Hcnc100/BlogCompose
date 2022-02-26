package com.nullpointer.blogcompose.ui.screens.authScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.nullpointer.blogcompose.R

@Composable
fun AuthScreen() {
    Scaffold(backgroundColor = MaterialTheme.colors.primary) {
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
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


            Column(modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.4f)
                .background(MaterialTheme.colors.error),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ExtendedFloatingActionButton(onClick = { /*TODO*/ },
                    text = { Text("Ingresa con Google") },
                    icon = {
                        Icon(painterResource(id = R.drawable.ic_google),
                            contentDescription = "",
                            modifier = Modifier.size(25.dp), tint = Color.Unspecified)
                    }, modifier = Modifier.width(250.dp),
                    backgroundColor = Color.White)
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