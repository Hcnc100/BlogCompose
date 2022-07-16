package com.nullpointer.blogcompose.ui.share

import android.content.Context

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coil.compose.rememberImagePainter
import com.nullpointer.blogcompose.BuildConfig
import com.nullpointer.blogcompose.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

import java.io.File

@Composable
fun SelectImgButtonSheet(
    actionHidden:() -> Unit,
    isVisible: Boolean,
    actionBeforeSelect: (Uri?) -> Unit,
) {
    BackHandler(enabled = isVisible, onBack = actionHidden)

    // ! this no work with delegate
    // * this is import for save state
    val tmpUri = rememberSaveable { mutableStateOf<Uri?>(null) }

    // * this work create a tmp file, and when no select, the tmp file is no null
    // * so, use the argument to know if take the photo or no
    val launcherPhoto = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { isCapture ->
        val response = if (isCapture) tmpUri.value else null
        actionBeforeSelect(response)
    }
    // * this only get uri of select image, is null if not select any image
    val launcherImg = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        actionBeforeSelect(uri)
    }
    val context = LocalContext.current


    Column(modifier = Modifier
        .fillMaxWidth()
        .height(160.dp)
        .clip(shape = RoundedCornerShape(10.dp))
        .padding(10.dp)
    ) {
        Text(
            text = stringResource(R.string.text_select_option),
            modifier = Modifier.padding(10.dp)
        )
        Spacer(modifier = Modifier.padding(vertical = 5.dp))
        ItemCamera {
            tmpUri.value = getTmpFileUri(context)
            launcherPhoto.launch(tmpUri.value)
        }
        ItemGallery {
            launcherImg.launch("image/*")
        }
    }
}




@Composable
private fun ItemGallery(
    modifier: Modifier = Modifier,
    actionLaunch: () -> Unit
) {
    ItemButtonSheet(
        iconResource = painterResource(id = R.drawable.ic_image),
        textOptionRes = stringResource(id = R.string.option_img_gallery),
        textDescriptionRes = stringResource(id = R.string.description_img_gallery),
        actionLaunch = actionLaunch,
        modifier = modifier
    )
}

@Composable
private fun ItemCamera(
    modifier: Modifier = Modifier,
    actionLaunch: () -> Unit,
) {
    ItemButtonSheet(
        iconResource = painterResource(id = R.drawable.ic_camera),
        textOptionRes = stringResource(id = R.string.option_img_camera),
        textDescriptionRes = stringResource(id = R.string.description_img_camera),
        actionLaunch = actionLaunch,
        modifier = modifier
    )
}

@Composable
private fun ItemButtonSheet(
    iconResource: Painter,
    textOptionRes: String,
    textDescriptionRes: String,
    actionLaunch: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier
        .fillMaxWidth()
        .clickable {
            actionLaunch()
        }
        .padding(10.dp)) {
        Icon(
            painter = iconResource,
            contentDescription = textDescriptionRes,
            tint = MaterialTheme.colors.onSurface
        )
        Spacer(modifier = Modifier.padding(horizontal = 8.dp))
        Text(text = textOptionRes)
    }
}

// * get cache file from system provider
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