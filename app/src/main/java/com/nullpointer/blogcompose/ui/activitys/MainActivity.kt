package com.nullpointer.blogcompose.ui.activitys

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.nullpointer.blogcompose.core.states.LoginStatus
import com.nullpointer.blogcompose.presentation.AuthViewModel
import com.nullpointer.blogcompose.ui.screens.NavGraphs
import com.nullpointer.blogcompose.ui.screens.destinations.AuthScreenDestination
import com.nullpointer.blogcompose.ui.screens.destinations.DataUserScreenDestination
import com.nullpointer.blogcompose.ui.screens.destinations.MainScreenDestination
import com.nullpointer.blogcompose.ui.screens.states.rememberRootAppState
import com.nullpointer.blogcompose.ui.theme.BlogComposeTheme
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.navigation.dependency
import com.ramcosta.composedestinations.spec.Route
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var loading = true
        val splash = installSplashScreen()
        splash.setKeepOnScreenCondition { loading }
        setContent {
            BlogComposeTheme {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colors.background
                ) {
                    val stateAuth = authViewModel.stateAuthUser.collectAsState()
                    val rootAppState = rememberRootAppState()

                    when (stateAuth.value) {
                        LoginStatus.Authenticated.CompleteData -> MainScreenDestination
                        LoginStatus.Authenticated.CompletingData -> DataUserScreenDestination
                        LoginStatus.Authenticating -> null
                        LoginStatus.Unauthenticated -> AuthScreenDestination
                    }?.let { startDestination ->
                        loading=false
                        DestinationsNavHost(
                            startRoute = startDestination,
                            navGraph = NavGraphs.main,
                            navController = rootAppState.navController,
                            engine = rootAppState.navHostEngine,
                            dependenciesContainerBuilder = {
                                dependency(authViewModel)
                                dependency(rootAppState.rootActions)
                            }
                        )
                    }
                }
            }
        }
    }
}
