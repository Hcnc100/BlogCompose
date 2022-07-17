package com.nullpointer.blogcompose.ui.screens.dataUser

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nullpointer.blogcompose.R
import com.nullpointer.blogcompose.ui.share.DotsTyping
import com.nullpointer.blogcompose.ui.share.LottieContainer

@Composable
fun CreatingDialog() {
    AlertDialog(
        onDismissRequest = {},
        buttons = {},
        title = {
            LottieContainer(modifier = Modifier.size(250.dp), R.raw.work)
        },
        text = {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.title_dialog_creating_user),
                    style = MaterialTheme.typography.body1,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.W500
                )
                DotsTyping(
                    modifier = Modifier.padding(vertical = 5.dp, horizontal = 10.dp),
                    color = (if (isSystemInDarkTheme()) Color.LightGray else Color.DarkGray).copy(alpha = 0.8f)
                )
            }

        },
        shape = RoundedCornerShape(20.dp)
    )
}