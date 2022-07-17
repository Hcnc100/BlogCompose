package com.nullpointer.blogcompose.ui.navigation

import com.nullpointer.blogcompose.R
import com.nullpointer.blogcompose.ui.screens.destinations.BlogScreenDestination
import com.nullpointer.blogcompose.ui.screens.destinations.NotifyScreenDestination
import com.nullpointer.blogcompose.ui.screens.destinations.ProfileScreenDestination
import com.ramcosta.composedestinations.spec.DirectionDestinationSpec

enum class HomeDestinations(
    val direction: DirectionDestinationSpec,
    val title: Int,
    val icon: Int,
    val description:Int
) {
    BlogScreen(
        direction = BlogScreenDestination,
        title = R.string.title_nav_post,
        icon = R.drawable.ic_home,
        description = R.string.description_nav_post
    ),

    NotifyScreen(
        direction = NotifyScreenDestination,
        title = R.string.title_nav_notify,
        icon = R.drawable.ic_notify,
        description = R.string.description_nav_notify
    ),

    ProfileScreen(
        direction = ProfileScreenDestination,
        title = R.string.title_nav_profile,
        icon = R.drawable.ic_account,
        description = R.string.description_nav_profile
    );

    companion object {
        private val listDestinations = listOf(
            BlogScreen,
            NotifyScreen,
            ProfileScreen
        )
    }

}