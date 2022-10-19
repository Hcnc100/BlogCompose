package com.nullpointer.blogcompose.ui.screens.states

import android.app.DownloadManager
import android.app.DownloadManager.Request
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import com.google.accompanist.swiperefresh.SwipeRefreshState
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.nullpointer.blogcompose.R
import kotlinx.coroutines.CoroutineScope

class BlogScreenState(
    context: Context,
    scope: CoroutineScope,
    focusManager: FocusManager,
    scaffoldState: ScaffoldState,
    listState: LazyListState,
    sizeScrollMore: Float,
    swipeState: SwipeRefreshState,
) : SwipeRefreshScreenState(
    scope = scope,
    context = context,
    listState = listState,
    focusManager = focusManager,
    scaffoldState = scaffoldState,
    sizeScrollMore = sizeScrollMore,
    swipeState = swipeState
) {

    private val downloadManager get() = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

    fun sharePost(idPost: String) {
        val intentChooser = Intent.createChooser(
            Intent(Intent.ACTION_SEND).apply {
                putExtra(
                    Intent.EXTRA_TEXT,
                    "https://www.blog-compose.com/post/$idPost"
                )
                type = "text/plain"
            },
            context.getString(R.string.title_share_post)
        )
        context.startActivity(intentChooser)
    }

    fun downloadPost(urlImg: String, idPost: String) {
        val request = Request(Uri.parse(urlImg)).apply {
            setDescription(context.getString(R.string.description_notify_download_post, idPost))
            setTitle(context.getString(R.string.title_notify_download_post))
            setDestinationInExternalPublicDir(Environment.DIRECTORY_DCIM, "BlogCompose/$idPost.jpg")
        }
        downloadManager.enqueue(request)
        Toast.makeText(
            context,
            context.getString(R.string.message_download_post),
            Toast.LENGTH_SHORT
        ).show()
    }
}

@Composable
fun rememberBlogScreenState(
    sizeScrollMore: Float,
    isRefreshing: Boolean,
    context: Context = LocalContext.current,
    scope: CoroutineScope = rememberCoroutineScope(),
    listState: LazyListState = rememberLazyListState(),
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    focusManager: FocusManager = LocalFocusManager.current,
    swipeState: SwipeRefreshState = rememberSwipeRefreshState(isRefreshing = isRefreshing)
) = remember(scope, listState, scaffoldState, swipeState) {
    BlogScreenState(
        scope = scope,
        context = context,
        listState = listState,
        swipeState = swipeState,
        focusManager = focusManager,
        scaffoldState = scaffoldState,
        sizeScrollMore = sizeScrollMore
    )
}