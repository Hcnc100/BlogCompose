package com.nullpointer.blogcompose.ui.screens.homeScreen.notifyScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import com.nullpointer.blogcompose.R

@Composable
fun NotifyScreen() {
    val listImages = listOf(
        "https://picsum.photos/500",
        "https://picsum.photos/500",
        "https://picsum.photos/500",
        "https://picsum.photos/500",
        "https://picsum.photos/500",
        "https://picsum.photos/500",
        "https://picsum.photos/500")
    Scaffold {
        LazyColumn {
            items(listImages.size) { index ->
                ItemNotify(urlImgProfile = listImages[index])
            }
        }
    }

}

@Composable
fun ItemNotify(
    urlImgProfile: String,
) {
    Card(modifier = Modifier.padding(vertical = 5.dp, horizontal = 5.dp),
        shape = RoundedCornerShape(10.dp)) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)) {
            val painter = rememberImagePainter(data = urlImgProfile) {
                transformations(CircleCropTransformation())
            }
            Image(painter = painter,
                contentDescription = "",
                modifier = Modifier
                    .size(60.dp)
                    .weight(2f)
                    .align(Alignment.CenterVertically))

            Spacer(modifier = Modifier.width(10.dp))

            TextNotifyInfo(modifier = Modifier.weight(5f))

            IconButton(onClick = { /*TODO*/ }, modifier = Modifier.weight(1f)) {
                Icon(painterResource(id = R.drawable.ic_options_horizontal), "")
            }

        }
    }
}

@Composable
fun TextNotifyInfo(
    modifier: Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Phasellus rhoncus nisi id tincidunt malesuada. In quis hendrerit nibh. In a commodo lectus. In orci purus, ultricies ",
            style = MaterialTheme.typography.body2,
            fontSize = 13.sp,
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(text = "hace 10 minutos", style = MaterialTheme.typography.caption)
    }


}
