package com.nullpointer.blogcompose.ui.screens.homeScreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.nullpointer.blogcompose.R
import com.nullpointer.blogcompose.presentation.AuthViewModel
import com.nullpointer.blogcompose.ui.navigation.HomeDestinations
import com.nullpointer.blogcompose.ui.navigation.MainNavHost
import timber.log.Timber

@Composable
fun HomeScreen() {
    val scaffoldState = rememberScaffoldState()
    val navController = rememberNavController()
    val navigationItems = listOf(
        HomeDestinations.BlogScreen,
        HomeDestinations.SearchScreen,
        HomeDestinations.NotifyScreen,
        HomeDestinations.ProfileScreen
    )

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(title = {
                Text(stringResource(id = R.string.app_name))
            })
        },
        bottomBar = {
            ButtonNavigation(
                items = navigationItems,
                navController = navController
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            MainNavHost(navController = navController)
        }

    }
}

@Composable
fun ButtonNavigation(
    items: List<HomeDestinations>,
    navController: NavController,
) {

    val currentRoute = currentRoute(navController = navController)

    BottomNavigation(
        backgroundColor = MaterialTheme.colors.primary,
        contentColor = Color.White
    ) {
        items.forEachIndexed { _, screens ->
            BottomNavigationItem(
                icon = { Icon(painterResource(screens.icon), contentDescription = screens.title) },
                label = {
                    Text(text = screens.title,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis)
                },
                alwaysShowLabel = false,
                selected = currentRoute == screens.route,
                onClick = {
                    navController.navigate(screens.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                })
        }
    }
}

@Composable
fun currentRoute(navController: NavController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}
