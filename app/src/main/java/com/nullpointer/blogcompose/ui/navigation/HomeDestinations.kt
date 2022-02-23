package com.nullpointer.blogcompose.ui.navigation

import androidx.compose.ui.graphics.BlendMode.Companion.Screen
import com.nullpointer.blogcompose.R

sealed class HomeDestinations(
    val route: String,
    val title: String,
    val icon: Int,
) {
    object BlogScreen : HomeDestinations(
        route = "HomeScreen",
        title = "Blogs",
        icon = R.drawable.ic_home
    )
    object SearchScreen : HomeDestinations(
        route = "SearchScreen",
        title = "Buscar",
        icon = R.drawable.ic_search
    )
    object NotifyScreen : HomeDestinations(
        route = "NotifyScreen",
        title = "Notificaciones",
        icon = R.drawable.ic_notify
    )
    object ProfileScreen : HomeDestinations(
        route = "PerfilScreen",
        title = "Perfil",
        icon = R.drawable.ic_account
    )
}