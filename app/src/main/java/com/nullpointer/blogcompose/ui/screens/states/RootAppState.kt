package com.nullpointer.blogcompose.ui.screens.states

import android.content.Context
import android.net.Uri
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.nullpointer.blogcompose.ui.interfaces.ActionRootDestinations
import com.ramcosta.composedestinations.animations.defaults.RootNavGraphDefaultAnimations
import com.ramcosta.composedestinations.animations.rememberAnimatedNavHostEngine
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.spec.Direction
import com.ramcosta.composedestinations.spec.NavHostEngine
import kotlinx.coroutines.CoroutineScope

class RootAppState(
    scaffoldState: ScaffoldState,
    context: Context,
    focusManager: FocusManager,
    val navController: NavHostController,
    val scope: CoroutineScope,

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
    scope: CoroutineScope= rememberCoroutineScope(),
    context: Context = LocalContext.current,
    focusManager: FocusManager = LocalFocusManager.current,
) = remember(scaffoldState, navController) {
    RootAppState(scaffoldState, context, focusManager, navController,scope)
}