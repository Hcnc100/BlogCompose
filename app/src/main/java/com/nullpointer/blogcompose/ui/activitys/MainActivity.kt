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
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.navigationBarsWithImePadding
import com.nullpointer.blogcompose.core.states.LoginStatus
import com.nullpointer.blogcompose.presentation.*
import com.nullpointer.blogcompose.ui.navigation.HomeDestinations
import com.nullpointer.blogcompose.ui.screens.NavGraphs
import com.nullpointer.blogcompose.ui.screens.destinations.AuthScreenDestination
import com.nullpointer.blogcompose.ui.screens.destinations.BlogScreenDestination
import com.nullpointer.blogcompose.ui.screens.destinations.DataUserScreenDestination
import com.nullpointer.blogcompose.ui.screens.navDestination
import com.nullpointer.blogcompose.ui.theme.BlogComposeTheme
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.navigation.dependency
import com.ramcosta.composedestinations.navigation.navigateTo
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val authViewModel:AuthViewModel by viewModels()
    private val myPostViewModel:MyPostViewModel by viewModels()
    private val postViewModel:PostViewModel by viewModels()
    private val notifyViewModel:NotifyViewModel by viewModels()

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
                    val initRoute = when (stateAuth.value) {
                        LoginStatus.Authenticating -> null
                        LoginStatus.Authenticated.CompleteData -> {
                            myPostViewModel.requestNewPost()
                            postViewModel.requestNewPost()
                            notifyViewModel.requestLastNotify()
                            BlogScreenDestination
                        }
                        LoginStatus.Authenticated.CompletingData -> DataUserScreenDestination
                        LoginStatus.Unauthenticated -> AuthScreenDestination
                    }
                    if (initRoute != null) {
                        navController.navigateTo(initRoute)
                        loading = false
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
                        Box(modifier = Modifier.padding(innerPadding)) {
                            DestinationsNavHost(
                                navController = navController,
                                navGraph = NavGraphs.root,
                                dependenciesContainerBuilder = {
                                    dependency(hiltViewModel<AuthViewModel>(this@MainActivity))
                                    dependency(hiltViewModel<NotifyViewModel>(this@MainActivity))
                                    dependency(hiltViewModel<MyPostViewModel>(this@MainActivity))
                                    dependency(hiltViewModel<PostViewModel>(this@MainActivity))
                                    dependency(hiltViewModel<RegistryViewModel>(this@MainActivity))
                                }
                            )
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
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { Icon(painterResource(id = destination.icon), "") },
                label = { Text(destination.title) },
            )
        }
    }
}

@Composable
fun currentRoute(navController: NavController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}

