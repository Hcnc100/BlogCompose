package com.nullpointer.blogcompose.ui.screens.homeScreen.blogScreen.componets

import android.content.Context
import android.content.Intent
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.annotation.ExperimentalCoilApi
import com.nullpointer.blogcompose.core.utils.TimeUtils
import com.nullpointer.blogcompose.models.SimplePost
import java.util.*

@OptIn(ExperimentalCoilApi::class)
@Composable
fun BlogItem(
    post: SimplePost,
    actionDetails: (String, Boolean) -> Unit,
    actionChangePost: (String, Boolean) -> Unit,
    staticInfo: Pair<String, String>? = null,
) {
    val context = LocalContext.current
    // * card content
    Card(
        modifier = Modifier
            .padding(10.dp)
            .wrapContentWidth(),
        shape = RoundedCornerShape(10.dp)
    ) {
        // * show static info or info for poster owner
        Column {
            // * info owner post
            if (staticInfo != null) {
                HeaderBlog(staticInfo.first, staticInfo.second)
            } else {
                HeaderBlog(post.poster!!.urlImg, post.poster!!.name)
            }
            // * image
            ImageBlog(
                urlImage = post.urlImage,
                actionToDetails = { actionDetails(post.id, false) }
            )
            // * buttons to interactive with post
            ButtonsInteractionBlog(
                ownerLike = post.ownerLike,
                actionShare = { sharePost(post.id, context) },
                actionComments = { actionDetails(post.id, true) },
                changeLike = { actionChangePost(post.id, it) })
            // * number of likes and comments
            TextLikes(post.numberLikes, post.numberComments)
            // * description, this is folding
            DescriptionBlog(Modifier.padding(5.dp), post.description)
            // * time to publish post
            TextTime(post.timestamp)
        }
    }
}

fun sharePost(idPost: String, context: Context) {
    val intent = Intent(Intent.ACTION_SEND)
        .putExtra(Intent.EXTRA_TEXT, "https://www.blog-compose.com/post/$idPost")
        .setType("text/plain")
    context.startActivity(Intent.createChooser(intent, "Compartir post"))
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
    // * folding text to description post
    val (isExpanded, changeExpanded) = rememberSaveable { mutableStateOf(false) }
    Text(text = description,
        maxLines = if (isExpanded) Int.MAX_VALUE else 2,
        overflow = if (isExpanded) TextOverflow.Visible else TextOverflow.Ellipsis,
        modifier = modifier.clickable { changeExpanded(!isExpanded) },
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



