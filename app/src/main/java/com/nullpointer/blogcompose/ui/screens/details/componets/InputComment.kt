package com.nullpointer.blogcompose.ui.screens.details.componets

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.nullpointer.blogcompose.R

@Composable
fun TextInputComment(focusRequester: FocusRequester, actionSendComment: (String) -> Unit) {
    val (text, changeText) = rememberSaveable { mutableStateOf("") }
    // * text to send comment
    Box {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
                .height(60.dp)
                .focusRequester(focusRequester),
            value = text,
            onValueChange = changeText,
            singleLine = true,
            label = { Text("Comentario") },
            shape = RoundedCornerShape(20.dp),
            placeholder = { Text("Escribe algo ...") },
            // * button to send action
            // * this trigger action and clear box text
            // * this action only is trigger if the comment is not empty
            trailingIcon = {
                IconButton(onClick = {
                    if (text.isNotEmpty()) {
                        actionSendComment(text)
                        changeText("")
                    }
                }) {
                    // * icon that change if comment is valid (if no is empty)
                    Icon(painterResource(id = R.drawable.ic_send),
                        contentDescription = "",
                        tint = if (text.isEmpty()) Color.Gray else MaterialTheme.colors.primary)
                }
            }
        )
    }
}