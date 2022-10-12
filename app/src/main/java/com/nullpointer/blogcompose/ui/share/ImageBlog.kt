package com.nullpointer.blogcompose.ui.share

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.nullpointer.blogcompose.R
import com.nullpointer.blogcompose.core.delegates.PropertySavableImg
import com.nullpointer.blogcompose.core.utils.getGrayColor
import com.nullpointer.blogcompose.core.utils.isSuccess

@Composable
fun EditableImage(
    sizeImage: Dp,
    modifier: Modifier = Modifier,
    imgUser: PropertySavableImg,
    sizePlaceHolder: Dp = sizeImage,
    actionChangePhoto: () -> Unit,
    @DrawableRes
    placeholder: Int = R.drawable.ic_image,
    @DrawableRes
    error: Int = R.drawable.ic_broken_image,
    contentDescription: String? = null,
    isCircular: Boolean
) {

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(sizeImage)
    ) {

        SimpleImage(
            error = error,
            image = imgUser.value,
            isCircular = isCircular,
            sizeImage = sizeImage,
            placeholder = placeholder,
            sizePlaceHolder = sizePlaceHolder,
            contentDescription = contentDescription,
            isEmpty = imgUser.isEmpty,
        )
        FloatingActionButton(
            onClick = actionChangePhoto,
            modifier = Modifier
                .padding(10.dp)
                .size(40.dp)
                .align(Alignment.BottomEnd)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_edit),
                contentDescription = stringResource(
                    id = R.string.change_image_user
                )
            )
        }

        if (imgUser.isCompress)
            CircularProgressIndicator(
                modifier = Modifier.size(50.dp),
                color = MaterialTheme.colors.primary,
                strokeWidth = 5.dp
            )
    }
}

@Composable
fun SimpleImage(
    image: Any,
    sizeImage: Dp,
    modifier: Modifier = Modifier,
    sizePlaceHolder: Dp = sizeImage,
    context: Context = LocalContext.current,
    @DrawableRes
    placeholder: Int = R.drawable.ic_image,
    @DrawableRes
    error: Int = R.drawable.ic_broken_image,
    contentDescription: String? = null,
    isEmpty: Boolean = false,
    isCircular: Boolean,
) {

    require(sizePlaceHolder <= sizeImage) {
        "The sizePlaceHolder must be less than the sizeImage"
    }

    val painter = rememberAsyncImagePainter(
        placeholder = painterResource(id = placeholder),
        error = painterResource(id = error),
        model = ImageRequest.Builder(context)
            .data(image)
            .crossfade(true).apply {
                if (isCircular) transformations(CircleCropTransformation())
            }
            .build(),
    )

    val paddingImg by animateDpAsState(targetValue = if (isEmpty) sizeImage - sizePlaceHolder else 0.dp)

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(sizeImage)
    ) {
        Card(
            shape = if (isCircular) CircleShape else MaterialTheme.shapes.medium,
            modifier = Modifier.fillMaxSize(),
        ) {
            Image(
                contentDescription = contentDescription,
                colorFilter = if (painter.isSuccess) null else ColorFilter.tint(getGrayColor()),
                painter = if (!isEmpty) painter else painterResource(id = placeholder),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingImg),
            )
        }

    }
}
