package com.nullpointer.blogcompose.ui.activitys

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.navigationBarsWithImePadding
import com.nullpointer.blogcompose.core.states.LoginStatus
import com.nullpointer.blogcompose.presentation.AuthViewModel
import com.nullpointer.blogcompose.ui.navigation.HomeDestinations
import com.nullpointer.blogcompose.ui.screens.NavGraphs
import com.nullpointer.blogcompose.ui.screens.destinations.AuthScreenDestination
import com.nullpointer.blogcompose.ui.screens.destinations.DataUserScreenDestination
import com.nullpointer.blogcompose.ui.screens.navDestination
import com.nullpointer.blogcompose.ui.screens.startDestination
import com.nullpointer.blogcompose.ui.theme.BlogComposeTheme
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.navigation.dependency
import com.ramcosta.composedestinations.navigation.navigateTo
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var loading = true
        val splash = installSplashScreen()
        splash.setKeepOnScreenCondition { loading }
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            BlogComposeTheme {
                ProvideWindowInsets(windowInsetsAnimationsEnabled = true) {

                    val navController = rememberNavController()
                    var isHomeRoute by remember { mutableStateOf(false) }

                    navController.addOnDestinationChangedListener { _, navDestination: NavDestination, _ ->
                        isHomeRoute = HomeDestinations.isHomeRoute(navDestination.route)
                    }


                    val stateAuth = authViewModel.stateAuthUser.collectAsState()
                    val destination = when (stateAuth.value) {
                        LoginStatus.Authenticated.CompleteData -> NavGraphs.homeDestinations
                        LoginStatus.Authenticated.CompletingData -> DataUserScreenDestination.startDestination
                        LoginStatus.Authenticating -> null
                        LoginStatus.Unauthenticated -> AuthScreenDestination.startDestination
                    }

                    Scaffold(modifier = Modifier
                        .navigationBarsWithImePadding()
                        .systemBarsPadding(),
                        bottomBar = {
                            AnimatedVisibility(
                                visible = isHomeRoute,
                                enter = slideInVertically(initialOffsetY = { it }),
                                exit = slideOutVertically(targetOffsetY = { it }),
                            ) {
                                ButtonNavigation(
                                    navController = navController
                                )
                            }
                        }
                    ) { innerPadding ->

                        if (destination != null) {
                            loading = false
                            Box(modifier = Modifier.padding(innerPadding)) {
                                DestinationsNavHost(
                                    navController = navController,
                                    navGraph = NavGraphs.root,
                                    startRoute = destination,
                                    dependenciesContainerBuilder = {
                                        dependency(authViewModel)
                                    }
                                )
                            }
                        }
                    }
                }


            }
        }
    }

}


@Composable
fun ButtonNavigation(
    navController: NavController,
) {


    val currentDestination = navController.currentBackStackEntryAsState()
        .value?.navDestination

    BottomNavigation {
        HomeDestinations.values().forEach { destination ->
            BottomNavigationItem(
                selected = currentDestination == destination.direction,
                onClick = {
                    navController.navigateTo(destination.direction) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(painterResource(id = destination.icon),
                        stringResource(id = destination.description))
                },
                label = { Text(stringResource(id = destination.title)) },
            )
        }
    }
}

@Composable
fun currentRoute(navController: NavController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}

