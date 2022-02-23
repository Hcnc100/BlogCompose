package com.nullpointer.blogcompose.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.nullpointer.blogcompose.ui.screens.homeScreen.blogScreen.BlogScreen
import com.nullpointer.blogcompose.ui.screens.homeScreen.notifyScreen.NotifyScreen
import com.nullpointer.blogcompose.ui.screens.homeScreen.profileScreen.ProfileScreen
import com.nullpointer.blogcompose.ui.screens.homeScreen.searchScreen.SearchScreen

@Composable
fun MainNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = HomeDestinations.BlogScreen.route) {
        composable(HomeDestinations.BlogScreen.route) {
            BlogScreen()
        }
        composable(HomeDestinations.SearchScreen.route) {
            SearchScreen()
        }
        composable(HomeDestinations.NotifyScreen.route) {
            NotifyScreen()
        }
        composable(HomeDestinations.ProfileScreen.route) {
            ProfileScreen()
        }
    }
}