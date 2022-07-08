package com.nullpointer.blogcompose.ui.screens.states

import android.content.Context
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
import com.ramcosta.composedestinations.spec.NavHostEngine

class MainScreenState(
    scaffoldState: ScaffoldState,
    context: Context,
    focusManager: FocusManager,
    val navController: NavHostController,
) : SimpleScreenState(scaffoldState, context, focusManager)

@Composable
fun rememberMainScreenState(
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    navController: NavHostController = rememberNavController(),
    context: Context = LocalContext.current,
    focusManager: FocusManager = LocalFocusManager.current,
) = remember(scaffoldState, navController) {
    MainScreenState(scaffoldState, context, focusManager, navController)
}
