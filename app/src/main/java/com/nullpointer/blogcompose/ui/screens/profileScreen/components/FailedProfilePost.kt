package com.nullpointer.blogcompose.ui.screens.profileScreen.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.nullpointer.blogcompose.R
import com.nullpointer.blogcompose.ui.share.LottieContainer

@Composable
fun FailedProfilePost() {
    Column {
        Spacer(modifier = Modifier.height(20.dp))
        LottieContainer(
            animation = R.raw.error3,
            modifier = Modifier
                .size(250.dp)
                .align(Alignment.CenterHorizontally)
        )
        Text(
            text = stringResource(id = R.string.error_load_my_post),
            Modifier.align(Alignment.CenterHorizontally)
        )
    }
}