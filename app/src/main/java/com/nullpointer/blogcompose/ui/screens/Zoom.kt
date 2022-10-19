package com.nullpointer.blogcompose.ui.screens

import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView
import coil.load
import com.jsibbold.zoomage.ZoomageView
import com.nullpointer.blogcompose.ui.navigation.MainNavGraph
import com.ramcosta.composedestinations.annotation.Destination

@MainNavGraph
@Destination
@Composable
fun ZoomScreen(
    urlImg: String
) {
    AndroidView(factory = {
        ZoomageView(it).apply {
            layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
            load(urlImg)
        }
    })

}