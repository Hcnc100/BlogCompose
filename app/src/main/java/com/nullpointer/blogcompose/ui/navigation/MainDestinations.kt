package com.nullpointer.blogcompose.ui.navigation

sealed class MainDestinations(
    val route:String
) {
    object LoginScreen:MainDestinations(
        "loginScreen"
    )
    object RegistryScreen:MainDestinations(
        "registryScreen"
    )
    object HomeScreen : MainDestinations(
        "HomeScreen"
    )
}