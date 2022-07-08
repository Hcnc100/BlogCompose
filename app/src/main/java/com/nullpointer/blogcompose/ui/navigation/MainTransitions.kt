package com.nullpointer.blogcompose.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.navigation.NavBackStackEntry
import com.nullpointer.blogcompose.ui.screens.appDestination
import com.nullpointer.blogcompose.ui.screens.destinations.BlogScreenDestination
import com.nullpointer.blogcompose.ui.screens.destinations.NotifyScreenDestination
import com.nullpointer.blogcompose.ui.screens.destinations.ProfileScreenDestination
import com.ramcosta.composedestinations.spec.DestinationStyle

@OptIn(ExperimentalAnimationApi::class)
object MainTransitions : DestinationStyle.Animated {
    override fun AnimatedContentScope<NavBackStackEntry>.enterTransition(): EnterTransition? {
        return when {
            initialState.appDestination() == BlogScreenDestination
                    && targetState.appDestination() == NotifyScreenDestination ->
                slideInHorizontally(
                    initialOffsetX = { 1000 },
                    animationSpec = tween(700)
                )

            initialState.appDestination() == NotifyScreenDestination
                    && targetState.appDestination() == BlogScreenDestination ->
                slideInHorizontally(
                    initialOffsetX = {- 1000 },
                    animationSpec = tween(700)
                )

            initialState.appDestination() == NotifyScreenDestination
                    && targetState.appDestination() == ProfileScreenDestination ->
                slideInHorizontally(
                    initialOffsetX = { 1000 },
                    animationSpec = tween(700)
                )

            initialState.appDestination() == ProfileScreenDestination
                    && targetState.appDestination() == NotifyScreenDestination ->
                slideInHorizontally(
                    initialOffsetX = {- 1000 },
                    animationSpec = tween(700)
                )

            initialState.appDestination() == BlogScreenDestination
                    && targetState.appDestination() == ProfileScreenDestination ->
                slideInHorizontally(
                    initialOffsetX = { 1000 },
                    animationSpec = tween(700)
                )


            else -> null
        }
    }

    override fun AnimatedContentScope<NavBackStackEntry>.exitTransition(): ExitTransition? {

        return when {

            initialState.appDestination() == BlogScreenDestination && targetState.appDestination() == NotifyScreenDestination ->
                slideOutHorizontally(
                    targetOffsetX = { -1000 },
                    animationSpec = tween(700)
                )

            initialState.appDestination() == NotifyScreenDestination && targetState.appDestination() == ProfileScreenDestination ->
                slideOutHorizontally(
                    targetOffsetX = { - 1000 },
                    animationSpec = tween(700)
                )

            initialState.appDestination() == ProfileScreenDestination && targetState.appDestination() == NotifyScreenDestination ->
                slideOutHorizontally(
                    targetOffsetX = { 1000 },
                    animationSpec = tween(700)
                )

            initialState.appDestination() == BlogScreenDestination
                    && targetState.appDestination() == ProfileScreenDestination ->
                slideOutHorizontally(
                    targetOffsetX = { - 1000 },
                    animationSpec = tween(700)
                )
            else -> null
        }
    }

    override fun AnimatedContentScope<NavBackStackEntry>.popEnterTransition(): EnterTransition? {

        return when {
            initialState.appDestination() == NotifyScreenDestination && targetState.appDestination() == BlogScreenDestination ->
                slideInHorizontally (
                    initialOffsetX = {- 1000 },
                    animationSpec = tween(700)
                )

            initialState.appDestination() == NotifyScreenDestination && targetState.appDestination() == ProfileScreenDestination ->
                slideInHorizontally(
                    initialOffsetX = {- 1000 },
                    animationSpec = tween(700)
                )

            initialState.appDestination() == ProfileScreenDestination
                    && targetState.appDestination() == BlogScreenDestination ->
                slideInHorizontally(
                    initialOffsetX = { -1000 },
                    animationSpec = tween(700)
                )
            else -> null
        }
    }

    override fun AnimatedContentScope<NavBackStackEntry>.popExitTransition(): ExitTransition? {

        return when {
            initialState.appDestination() == NotifyScreenDestination && targetState.appDestination() == BlogScreenDestination ->
                slideOutHorizontally(
                    targetOffsetX = { 1000 },
                    animationSpec = tween(700)
                )

            initialState.appDestination() == NotifyScreenDestination && targetState.appDestination() == ProfileScreenDestination ->
                slideOutHorizontally(
                    targetOffsetX = { 1000 },
                    animationSpec = tween(7000)
                )

            initialState.appDestination() == ProfileScreenDestination
                    && targetState.appDestination() == BlogScreenDestination ->
                slideOutHorizontally(
                    targetOffsetX = {1000 },
                    animationSpec = tween(700)
                )
            else -> null
        }
    }
}