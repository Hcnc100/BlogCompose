package com.nullpointer.blogcompose.ui.share

import androidx.compose.animation.*
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.nullpointer.blogcompose.R

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ButtonAdd(
    modifier: Modifier = Modifier,
    isScrollInProgress: Boolean,
    action: () -> Unit,
) {
    AnimatedVisibility(
        modifier = modifier,
        visible = !isScrollInProgress,
        enter = scaleIn() + fadeIn(),
        exit = scaleOut() + fadeOut()
    ) {
        FloatingActionButton(onClick = { action() }) {
            Icon(painterResource(id = R.drawable.ic_add),
                contentDescription = "")
        }
    }
}