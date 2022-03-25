package com.nullpointer.blogcompose.ui.share

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.annotation.ExperimentalCoilApi
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter
import coil.size.OriginalSize
import coil.transform.CircleCropTransformation
import com.nullpointer.blogcompose.R
import java.io.File


@OptIn(ExperimentalCoilApi::class)
@Composable
fun ImageProfile(
    urlImgProfile: String,
    paddingLoading: Dp,
    sizeImage: Dp,
    modifier: Modifier = Modifier,
    fileImg: File? = null,
    showProgress: Boolean = false,
) {
    val painter = rememberImagePainter(
        // * if pass file img so ,load first,
        // * else load urlImg if this is not empty
        // * else load default
        data = when {
            fileImg != null -> fileImg
            urlImgProfile.isNotEmpty() -> urlImgProfile
            else -> R.drawable.ic_person
        }
    ) {
        transformations(CircleCropTransformation())
        placeholder(R.drawable.ic_person)
        size(OriginalSize)
        crossfade(true)
    }
    val state = painter.state
    Box(contentAlignment = Alignment.Center,
        modifier = modifier.size(sizeImage)) {
        Image(
            painter = when (state) {
                is ImagePainter.State.Error -> painterResource(id = R.drawable.ic_broken_image)
                else -> painter
            },
            contentDescription = "",
            modifier = Modifier
                .fillMaxSize()
                .padding(if (state !is ImagePainter.State.Success) paddingLoading else 0.dp),
        )
        if (state is ImagePainter.State.Loading && showProgress) CircularProgressIndicator()
    }
}

@OptIn(ExperimentalCoilApi::class)
@Composable
fun ImagePost(
    paddingLoading: Dp,
    modifier: Modifier = Modifier,
    urlImgPost: String? = null,
    fileImg: File? = null,
    showProgress: Boolean = false,
) {
    val painter = rememberImagePainter(
        // * if pass file img so ,load first,
        // * else load urlImg if this is not empty
        // * else load default
        data = when {
            fileImg != null -> fileImg
            !urlImgPost.isNullOrEmpty() -> urlImgPost
            else -> R.drawable.ic_image
        }
    ) {
        placeholder(R.drawable.ic_image)
        crossfade(true)
    }
    val state = painter.state
    Box(contentAlignment = Alignment.Center,
        modifier = modifier.padding(
            if (state !is ImagePainter.State.Success || fileImg == null && urlImgPost.isNullOrEmpty())
                paddingLoading else 0.dp)) {
        Image(
            painter = when (state) {
                is ImagePainter.State.Error -> painterResource(id = R.drawable.ic_broken_image)
                else -> painter
            },
            contentDescription = "",
            modifier = Modifier.fillMaxSize(),
        )
        if (state is ImagePainter.State.Loading && showProgress) CircularProgressIndicator()
    }
}