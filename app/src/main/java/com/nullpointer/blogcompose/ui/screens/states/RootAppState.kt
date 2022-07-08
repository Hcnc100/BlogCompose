package com.nullpointer.blogcompose.ui.screens.states

import android.content.Context
import android.net.Uri
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.nullpointer.blogcompose.core.utils.SimpleScreenState
import com.nullpointer.blogcompose.ui.interfaces.ActionRootDestinations
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.spec.Direction

class RootAppState(
    scaffoldState: ScaffoldState,
    context: Context,
    focusManager: FocusManager,
    val navController: NavHostController,
) : SimpleScreenState(scaffoldState, context, focusManager) {
    val rootActions = object : ActionRootDestinations {
        override fun backDestination() = navController.popBackStack()
        override fun changeRoot(direction: Direction) = navController.navigate(direction)
        override fun changeRoot(route: Uri) = navController.navigate(route)
    }
}

@Composable
fun rememberRootAppState(
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    navController: NavHostController = rememberNavController(),
    context: Context = LocalContext.current,
    focusManager: FocusManager = LocalFocusManager.current,
) = remember(scaffoldState, navController) {
    RootAppState(scaffoldState, context, focusManager, navController)
}