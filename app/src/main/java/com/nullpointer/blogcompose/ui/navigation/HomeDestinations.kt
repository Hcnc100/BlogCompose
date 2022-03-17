package com.nullpointer.blogcompose.ui.navigation

import com.nullpointer.blogcompose.R
import com.nullpointer.blogcompose.ui.screens.destinations.BlogScreenDestination
import com.nullpointer.blogcompose.ui.screens.destinations.NotifyScreenDestination
import com.nullpointer.blogcompose.ui.screens.destinations.ProfileScreenDestination
import com.ramcosta.composedestinations.spec.DirectionDestinationSpec

enum class HomeDestinations(
    val direction: DirectionDestinationSpec,
    val title: String,
    val icon: Int,
) {
    BlogScreen(
        direction = BlogScreenDestination,
        title = "Blogs",
        icon = R.drawable.ic_home
    ),

    NotifyScreen(
        direction = NotifyScreenDestination,
        title = "Notificaciones",
        icon = R.drawable.ic_notify
    ),

    ProfileScreen(
        direction = ProfileScreenDestination,
        title = "Perfil",
        icon = R.drawable.ic_account
    );

    companion object {
        private val listDestinations = listOf(
            BlogScreen,
            NotifyScreen,
            ProfileScreen
        )

        fun isHomeRoute(route: String?): Boolean {
            if (route == null) return false
            return listDestinations.find { it.direction.route == route } != null
        }
    }

}