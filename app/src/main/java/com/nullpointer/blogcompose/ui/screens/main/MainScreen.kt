package com.nullpointer.blogcompose.ui.screens.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.currentBackStackEntryAsState
import com.nullpointer.blogcompose.presentation.AuthViewModel
import com.nullpointer.blogcompose.ui.interfaces.ActionRootDestinations
import com.nullpointer.blogcompose.ui.navigation.HomeDestinations
import com.nullpointer.blogcompose.ui.navigation.MainTransitions
import com.nullpointer.blogcompose.ui.navigation.RootNavGraph
import com.nullpointer.blogcompose.ui.screens.NavGraphs
import com.nullpointer.blogcompose.ui.screens.states.MainScreenState
import com.nullpointer.blogcompose.ui.screens.states.rememberMainScreenState
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.dependency
import com.ramcosta.composedestinations.navigation.navigate

@RootNavGraph
@Destination(style = MainTransitions::class)
@Composable
fun MainScreen(
    authViewModel: AuthViewModel,
    actionsRootDestinations: ActionRootDestinations,
    mainScreenState: MainScreenState = rememberMainScreenState()
) {
    Scaffold(
        bottomBar = {
            MainButtonNavigation(navController = mainScreenState.navController)
        }
    ){ innerPadding ->
        DestinationsNavHost(
            modifier = Modifier.padding(innerPadding),
            navController = mainScreenState.navController,
            engine = mainScreenState.navHostEngine,
            navGraph = NavGraphs.home,
            startRoute = NavGraphs.home.startRoute,
            dependenciesContainerBuilder = {
                dependency(authViewModel)
                dependency(actionsRootDestinations)
            }
        )

    }
}

@Composable
private fun MainButtonNavigation(
    navController: NavController,
) {
    val currentDestination = navController.currentBackStackEntryAsState()
        .value?.destination
    BottomNavigation {
        HomeDestinations.values().forEach { destination ->
            BottomNavigationItem(
                selected = currentDestination == destination.direction,
                onClick = {
                    navController.navigate(destination.direction, fun NavOptionsBuilder.() {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    })
                },
                icon = {
                    Icon(
                        painterResource(id = destination.icon),
                        stringResource(id = destination.description)
                    )
                },
                label = { Text(stringResource(id = destination.title)) },
            )
        }
    }
}
