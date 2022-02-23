package com.nullpointer.blogcompose.ui.screens.homeScreen.blogScreen.componets

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
            ButtonsInteractionBlog()
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
        },
        style = MaterialTheme.typography.body1

    )

}


@Composable
fun TextLikes() {
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 5.dp)
        .clickable { },
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



