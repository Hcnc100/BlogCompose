package com.nullpointer.blogcompose.ui.screens.homeScreen.profileScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import com.nullpointer.blogcompose.presentation.AuthViewModel

@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel = hiltViewModel(),
) {
    Scaffold {
        InfoProfile(authViewModel.photoUser)
    }
}

@Composable
fun InfoProfile(
    urlImgProfile:String
) {
    Column(modifier = Modifier.padding(horizontal = 10.dp)) {
        Row {
            val painter = rememberImagePainter(data = urlImgProfile) {
                transformations(CircleCropTransformation())
            }

            Image(painter = painter, contentDescription = "", modifier = Modifier
                .size(150.dp)
                .weight(1f))

            Row(modifier = Modifier
                .weight(2f)
                .height(150.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround) {
                StatisticText(name = "Post", value = "6")
                StatisticText(name = "Followers", value = "6")
                StatisticText(name = "Following", value = "6")
            }
        }
        Text(text = "Un nombre algo largo", style = MaterialTheme.typography.body1)
    }
}


@Composable
fun StatisticText(
    name: String,
    value: String,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.body1, fontWeight = FontWeight.W600)
        Text(name, style = MaterialTheme.typography.body2)
    }
}