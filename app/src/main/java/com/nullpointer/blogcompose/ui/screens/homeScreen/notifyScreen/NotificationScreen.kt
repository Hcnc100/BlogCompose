package com.nullpointer.blogcompose.ui.screens.homeScreen.notifyScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberImagePainter
import coil.transform.CircleCropTransformation
import com.nullpointer.blogcompose.R
import com.nullpointer.blogcompose.core.states.Resource
import com.nullpointer.blogcompose.core.utils.TimeUtils
import com.nullpointer.blogcompose.presentation.NotifyViewModel

@Composable
fun NotifyScreen(
    notifyVM: NotifyViewModel = hiltViewModel(),
) {
    val notifyState = notifyVM.listNotify.collectAsState()
    Scaffold {
        when (val state = notifyState.value) {
            is Resource.Failure -> {}
            is Resource.Loading -> {}
            is Resource.Success -> {
                val listNotify = state.data
                LazyColumn {
                    items(listNotify.size) { index ->
                        val post = listNotify[index]
                        ItemNotify(
                            imgPost = post.urlImgPost,
                            imgProfile = post.imgUserLiked,
                            nameLiked = post.nameUserLiked,
                            timeStamp = post.timestamp?.time ?: System.currentTimeMillis(),
                            isOpen = post.isOpen,
                        )
                    }
                }
            }
        }

    }

}

@Composable
fun ItemNotify(
    imgProfile: String,
    imgPost: String,
    nameLiked: String,
    timeStamp: Long,
    isOpen: Boolean,
) {
    Card(modifier = Modifier.padding(vertical = 5.dp, horizontal = 5.dp),
        shape = RoundedCornerShape(10.dp)) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)) {
            val painter = rememberImagePainter(data = imgProfile) {
                transformations(CircleCropTransformation())
            }
            Image(painter = painter,
                contentDescription = "",
                modifier = Modifier
                    .size(60.dp)
                    .weight(2f)
                    .align(Alignment.CenterVertically))

            Spacer(modifier = Modifier.width(10.dp))

            TextNotifyInfo(modifier = Modifier.weight(5f),
                nameLiked = nameLiked,
                timeStamp = timeStamp)

            Image(painter = rememberImagePainter(data = imgPost),
                contentDescription = "",
                modifier = Modifier
                    .size(60.dp)
                    .weight(2f)
                    .align(Alignment.CenterVertically))

        }
    }
}

@Composable
fun TextNotifyInfo(
    modifier: Modifier,
    nameLiked: String,
    timeStamp: Long,
) {
    val context = LocalContext.current
    Column(modifier = modifier) {
        Text(
            text = "A $nameLiked le gusta tu post",
            style = MaterialTheme.typography.body2,
            fontSize = 13.sp,
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(text = TimeUtils.getTimeAgo(timeStamp, context),
            style = MaterialTheme.typography.caption)
    }


}
