package com.nullpointer.blogcompose.ui.screens.details.componets.others

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.nullpointer.blogcompose.R
import com.nullpointer.blogcompose.core.delegates.PropertySavableString
import com.nullpointer.blogcompose.ui.share.EditableTextSavable

@Composable
fun TextInputComment(
    modifier: Modifier,
    actionSend: () -> Unit,
    valueProperty: PropertySavableString
) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        EditableTextSavable(
            valueProperty = valueProperty,
            shape = RoundedCornerShape(15.dp),
            modifier = modifier.weight(0.8f),
            keyboardActions = KeyboardActions(onSend = { actionSend() }),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Send)
        )
        IconButton(
            onClick = actionSend,
            enabled = !valueProperty.isEmpty
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_send),
                contentDescription = stringResource(id = R.string.description_send_comment),
                tint = if (valueProperty.hasChanged) MaterialTheme.colors.primary else Color.Unspecified
            )
        }
    }
}





