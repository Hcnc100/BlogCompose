package com.nullpointer.blogcompose.ui.share

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.nullpointer.blogcompose.core.delegates.PropertySavableString

@Composable
fun EditableTextSavable(
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
    singleLine: Boolean = false,
    valueProperty: PropertySavableString,
    shape: Shape = MaterialTheme.shapes.small,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
) {


    Surface(
        modifier = modifier.height(80.dp),
    ) {

        Column {
            OutlinedTextField(
                shape = shape,
                enabled = isEnabled,
                singleLine = singleLine,
                isError = valueProperty.hasError,
                keyboardOptions = keyboardOptions,
                keyboardActions = keyboardActions,
                value = valueProperty.currentValue,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(.8f),
                onValueChange = valueProperty::changeValue,
                visualTransformation = visualTransformation,
                label = { Text(stringResource(id = valueProperty.label)) },
                placeholder = { Text(stringResource(id = valueProperty.hint)) },
            )
            Row {
                Text(
                    color = MaterialTheme.colors.error,
                    style = MaterialTheme.typography.caption,
                    modifier = Modifier.weight(.9f),
                    text = if (valueProperty.hasError) stringResource(id = valueProperty.errorValue) else ""
                )
                Text(
                    text = valueProperty.countLength,
                    style = MaterialTheme.typography.caption,
                    color = if (valueProperty.hasError) MaterialTheme.colors.error else Color.Unspecified
                )
            }
        }
    }

}
