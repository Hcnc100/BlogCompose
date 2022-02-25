package com.nullpointer.blogcompose.ui.screens.addPost.components

import android.content.Context

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coil.compose.rememberImagePainter
import com.nullpointer.blogcompose.BuildConfig
import com.nullpointer.blogcompose.R

import java.io.File

@Composable
fun ButtonSheetContent(
    actionBeforeSelect: (Uri) -> Unit,
) {
    // ! this no work with delegate
    // * this is import for save state
    val tmpUri = rememberSaveable { mutableStateOf<Uri?>(null) }
    val launcherPhoto =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) {
            tmpUri.value?.let { actionBeforeSelect(it) }
        }
    val launcherImg =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let { actionBeforeSelect(it) }
        }
    val context = LocalContext.current

    Box(modifier = Modifier
        .fillMaxWidth()
        .height(160.dp)
        .clip(shape = RoundedCornerShape(10.dp))
        .padding(10.dp)

    ) {

        Column {

            Text(text = stringResource(R.string.text_select_option), modifier = Modifier.padding(10.dp))
            Spacer(modifier = Modifier.padding(vertical = 5.dp))
            ItemButtonSheet(
                iconResource = R.drawable.ic_camera,
                textOptionRes = R.string.option_img_camera,
                textDescriptionRes = R.string.description_img_camera
            ) {
                tmpUri.value = getTmpFileUri(context)
                launcherPhoto.launch(tmpUri.value)
            }
            ItemButtonSheet(
                iconResource = R.drawable.ic_image,
                textOptionRes = R.string.option_img_gallery,
                textDescriptionRes = R.string.description_img_gallery
            ) { launcherImg.launch("image/*") }
        }

    }
}


@Composable
fun ItemButtonSheet(
    @DrawableRes
    iconResource: Int,
    @StringRes
    textOptionRes: Int,
    @StringRes
    textDescriptionRes: Int,
    actionLaunch: () -> Unit,
) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .clickable {
            actionLaunch()
        }
        .padding(10.dp)) {
        Icon(
            rememberImagePainter(data = iconResource),
            contentDescription = stringResource(id = textDescriptionRes),
            tint = MaterialTheme.colors.onSurface
        )
        Spacer(modifier = Modifier.padding(horizontal = 8.dp))
        Text(text = stringResource(id = textOptionRes))
    }
}

private fun getTmpFileUri(context: Context): Uri {
    val tmpFile = File.createTempFile("tmp_image_file", ".png").apply {
        createNewFile()
        deleteOnExit()
    }
    return FileProvider.getUriForFile(
        context,
        "${BuildConfig.APPLICATION_ID}.provider",
        tmpFile
    )
}