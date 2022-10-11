package com.nullpointer.blogcompose.ui.share

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nullpointer.blogcompose.models.customSnack.MessageSnack

@Composable
fun CustomSnackBar(
    hostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    SnackbarHost(
        modifier = modifier,
        hostState = hostState,
        snackbar = { snackBarData: SnackbarData ->

            val messageDecode = remember {
                MessageSnack.decode(snackBarData.message)
            }

            Surface(
                contentColor = Color.White,
                color = messageDecode.type.color,
                shape = CircleShape,
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 10.dp, horizontal = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = messageDecode.type.icon,
                        contentDescription = "",
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = stringResource(id = messageDecode.msgResource),
                        style = MaterialTheme.typography.caption.copy(
                            fontSize = 14.sp
                        )
                    )
                }
            }
        }
    )
}