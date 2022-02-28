package com.nullpointer.blogcompose.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.nullpointer.blogcompose.core.states.LoginStatus
import com.nullpointer.blogcompose.presentation.AuthViewModel
import com.nullpointer.blogcompose.ui.screens.authScreen.AuthScreen
import com.nullpointer.blogcompose.ui.screens.dataUser.DataUserScreen
import com.nullpointer.blogcompose.ui.screens.homeScreen.HomeScreen

@Composable
fun HomeNavHost(
    navController: NavHostController = rememberNavController(),
    finishLoading: () -> Unit,
) {
    val viewModelStoreOwner = checkNotNull(LocalViewModelStoreOwner.current) {
        "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
    }
    val authViewModel: AuthViewModel = hiltViewModel(viewModelStoreOwner)
    val loginStatus = authViewModel.stateAuthUser.collectAsState()

    val initialRoute: String? = when (loginStatus.value) {
        LoginStatus.Authenticated -> {
            if (authViewModel.isDataComplete)
                MainDestinations.HomeScreen.route
            else
                MainDestinations.RegistryScreen.route
        }
        LoginStatus.Authenticating -> null
        LoginStatus.Unauthenticated -> {
            MainDestinations.LoginScreen.route
        }
    }
    if (initialRoute != null) {
        finishLoading()
        NavHost(navController = navController, startDestination = initialRoute) {
            composable(MainDestinations.LoginScreen.route) {
                AuthScreen()
            }
            composable(MainDestinations.RegistryScreen.route) {
                DataUserScreen()
            }
            composable(MainDestinations.HomeScreen.route){
                CompositionLocalProvider(
                    LocalViewModelStoreOwner provides viewModelStoreOwner
                ) {
                    HomeScreen()
                }
            }
        }
    }

}