package com.nullpointer.blogcompose.ui.screens.homeScreen.blogScreen.componets

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.annotation.ExperimentalCoilApi
import com.nullpointer.blogcompose.core.utils.TimeUtils
import com.nullpointer.blogcompose.models.Post
import com.nullpointer.blogcompose.models.SimplePost
import com.nullpointer.blogcompose.ui.screens.destinations.PostDetailsDestination
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import java.util.*

@OptIn(ExperimentalCoilApi::class)
@Composable
fun BlogItem(
    post: SimplePost,
    actionDetails: (String) -> Unit,
    actionChangePost: (String, Boolean) -> Unit,
    staticInfo: Pair<String, String>? = null,
) {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .padding(10.dp)
            .wrapContentWidth(),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column {
            if (staticInfo != null) {
                HeaderBlog(staticInfo.first, staticInfo.second)
            } else {
                HeaderBlog(post.poster!!.urlImg, post.poster!!.name)
            }
            ImageBlog(post.urlImage)
            ButtonsInteractionBlog(post.ownerLike, actionShare = {
                val intent = Intent(Intent.ACTION_SEND)
                    .putExtra(Intent.EXTRA_TEXT, "https://www.blog-compose.com/post/${post.id}")
                    .setType("text/plain")
                context.startActivity(Intent.createChooser(intent, "Share Using"))

            }, actionComments = {
                actionDetails(post.id)
            }) {
                actionChangePost(post.id, it)
            }
            TextLikes(post.numberLikes, post.numberComments)
            DescriptionBlog(Modifier.padding(5.dp), post.description)
            TextTime(post.timestamp)
        }
    }
}

@Composable
fun TextTime(timeStamp: Date?) {
    val context = LocalContext.current
    Text(text = TimeUtils.getTimeAgo(timeStamp?.time ?: 0, context),
        style = MaterialTheme.typography.caption,
        modifier = Modifier.padding(10.dp))
}


@Composable
fun DescriptionBlog(
    modifier: Modifier,
    description: String,
) {
    val (isExpanded, changeExpanded) = rememberSaveable { mutableStateOf(false) }
    Text(text = description,
        maxLines = if (isExpanded) Int.MAX_VALUE else 2,
        overflow = if (isExpanded) TextOverflow.Visible else TextOverflow.Ellipsis,
        modifier = modifier.clickable {
            changeExpanded(!isExpanded)
        },
        style = MaterialTheme.typography.body1

    )

}


@Composable
fun TextLikes(numberLikes: Int, numberComments: Int) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 5.dp)
        .clickable { },
        horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = "$numberLikes Me gusta",
            modifier = Modifier.padding(vertical = 5.dp),
            style = MaterialTheme.typography.caption,
            fontWeight = FontWeight.W400,
            fontSize = 14.sp
        )
        Text(text = "$numberComments Comentarios",
            modifier = Modifier.padding(vertical = 5.dp),
            style = MaterialTheme.typography.caption,
            fontWeight = FontWeight.W400,
            fontSize = 14.sp
        )
    }

}



