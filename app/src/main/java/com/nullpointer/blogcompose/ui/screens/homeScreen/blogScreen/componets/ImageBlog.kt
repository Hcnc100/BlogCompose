package com.nullpointer.blogcompose.ui.screens.homeScreen.blogScreen.componets

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.annotation.ExperimentalCoilApi
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter
import com.nullpointer.blogcompose.R

@ExperimentalCoilApi
@Composable
fun ImageBlog(urlImage: String) {
    val painter = rememberImagePainter(data = urlImage, builder = {
        crossfade(true)
    })
    val statePainter = painter.state
    Box(contentAlignment = Alignment.Center) {
        SquareImage(painter = painter)
        when (statePainter) {
            is ImagePainter.State.Loading -> Box(contentAlignment = Alignment.Center) {
                SquareImage(
                    modifier = Modifier.size(150.dp),
                    painter = painterResource(id = R.drawable.ic_image))
                CircularProgressIndicator()
            }
            is ImagePainter.State.Error -> SquareImage(
                modifier = Modifier.size(150.dp),
                painter = painterResource(id = R.drawable.ic_broken_image))
        }
    }

}

@Composable
fun SquareImage(
    modifier: Modifier = Modifier,
    painter: Painter,
) {
    Image(painter = painter,
        contentDescription = stringResource(R.string.description_img_blog),
        contentScale = ContentScale.Crop,
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f))
}

