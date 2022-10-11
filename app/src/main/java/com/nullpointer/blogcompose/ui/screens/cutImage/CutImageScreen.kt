package com.nullpointer.blogcompose.ui.screens.cutImage

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.canhub.cropper.CropImageView
import com.google.accompanist.insets.ui.Scaffold
import com.nullpointer.blogcompose.core.utils.shareViewModel
import com.nullpointer.blogcompose.presentation.RegistryViewModel
import com.nullpointer.blogcompose.ui.navigation.MainNavGraph
import com.ramcosta.composedestinations.annotation.Destination

@MainNavGraph
@Destination
@Composable
fun CutImageScreen(
    uriOriginal: Uri,
    registryViewModel: RegistryViewModel = shareViewModel(),
) {

    Scaffold(
        topBar = {
            TopAppBar(
                actions = {
                    TextButton(onClick = { /*TODO*/ }) {
                        Text("Hola")
                    }
                },
                title = { Text("Cut Image") },
            )
        }
    ) {
        CropImageViewComposable(actionCropSuccess = {}, uriOriginal = uriOriginal)
    }
}

@Composable
fun CropImageViewComposable(
    modifier: Modifier = Modifier,
    actionCropSuccess: (Bitmap) -> Unit,
    uriOriginal: Uri,
    context: Context = LocalContext.current
) {


    Box {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = {
                val bitmap = MediaStore.Images.Media.getBitmap(it.contentResolver, uriOriginal)
                CropImageView(context = context).apply {
                    layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                    setImageBitmap(bitmap)
                }
            },
        )

        Button(onClick = {}) {
            Text(text = "Hola")
        }
    }

}