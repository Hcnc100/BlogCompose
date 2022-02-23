package com.nullpointer.blogcompose.ui.screens.blogScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import com.nullpointer.blogcompose.R

@Composable
fun BlogItem() {
    Card(
        modifier = Modifier
            .padding(10.dp)
            .wrapContentWidth(),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column {
            HeaderBlog()
            ImageBlog()
            InteractionBlog()
            TextLikes()
            DescriptionBlog(Modifier.padding(5.dp))
            TextTime()
        }
    }
}

@Composable
fun TextTime() {
    Text(text = "Hace 10 minutos",
        style = MaterialTheme.typography.caption,
        modifier = Modifier.padding(10.dp))
}


@Composable
fun DescriptionBlog(
    modifier: Modifier,
) {
    val (isExpanded, changeExpanded) = rememberSaveable { mutableStateOf(false) }
    Text(text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer auctor pellentesque fermentum. Etiam eu lorem nec eros fringilla pretium. Proin sit amet sagittis nulla, sed lacinia turpis. Fusce id magna nec urna sollicitudin malesuada. Aenean bibendum quam at ipsum aliquam porttitor. Mauris vel venenatis ligula. Aliquam enim erat, porta a massa in, luctus aliquam elit. Donec id quam vitae erat ultricies auctor eu ac arcu. In sed blandit nibh. In consectetur iaculis lacinia.",
        maxLines = if (isExpanded) Int.MAX_VALUE else 2,
        overflow = if (isExpanded) TextOverflow.Visible else TextOverflow.Ellipsis,
        modifier = modifier.clickable {
            changeExpanded(!isExpanded)
        })

}

@Composable
fun ImageBlog() {
    Image(painter = rememberImagePainter(data = R.drawable.ic_launcher_background),
        contentDescription = "",
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f))
}

@Composable
fun TextLikes() {
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = "541 Me gusta",
            modifier = Modifier.padding(vertical = 5.dp),
            style = MaterialTheme.typography.caption,
            fontWeight = FontWeight.W400,
            fontSize = 14.sp
        )
        Text(text = "10 Comentarios",
            modifier = Modifier.padding(vertical = 5.dp),
            style = MaterialTheme.typography.caption,
            fontWeight = FontWeight.W400,
            fontSize = 14.sp
        )
    }

}

@Composable
fun InteractionBlog() {
    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {

        Row {
            IconButton(onClick = { /*TODO*/ }) {
                Icon(painterResource(id = R.drawable.ic_fav), "")
            }
            IconButton(onClick = { /*TODO*/ }) {
                Icon(painterResource(id = R.drawable.ic_comment), "")
            }
            IconButton(onClick = { /*TODO*/ }) {
                Icon(painterResource(id = R.drawable.ic_share), "")
            }
        }




        Row {
            IconButton(onClick = { /*TODO*/ }) {
                Icon(painterResource(id = R.drawable.ic_download), "")
            }
            IconButton(onClick = { /*TODO*/ }) {
                Icon(painterResource(id = R.drawable.ic_bookmark), "")
            }
        }


    }

}

@Composable
fun HeaderBlog() {

    Row(
        modifier = Modifier.padding(7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val painter = rememberImagePainter(
            data = "https://picsum.photos/200", builder = {
                transformations(CircleCropTransformation())
                crossfade(true)
                placeholder(R.drawable.ic_account)
            }
        )
        Image(painter = painter,
            contentDescription = "Imagen del usuario",
            modifier = Modifier
                .size(40.dp)
                .weight(1f)
        )
        Spacer(modifier = Modifier
            .width(10.dp))
        Text(text = "Hola como estas este es un nombre de usuario muy largo y no va a caber",
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(5f)
        )

        IconButton(onClick = { /*TODO*/ }, modifier = Modifier
            .weight(1f)
            .size(20.dp)) {
            Icon(painterResource(id = R.drawable.ic_options_vertical),
                "")
        }
    }
}