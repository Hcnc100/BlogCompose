package com.nullpointer.blogcompose.ui.activitys

import android.os.Bundle
import android.window.SplashScreen
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.navigationBarsWithImePadding
import com.nullpointer.blogcompose.core.states.LoginStatus
import com.nullpointer.blogcompose.presentation.AuthViewModel
import com.nullpointer.blogcompose.ui.navigation.HomeNavHost
import com.nullpointer.blogcompose.ui.screens.authScreen.AuthScreen
import com.nullpointer.blogcompose.ui.screens.dataUser.DataUserScreen
import com.nullpointer.blogcompose.ui.screens.homeScreen.HomeScreen
import com.nullpointer.blogcompose.ui.screens.homeScreen.profileScreen.ProfileScreen
import com.nullpointer.blogcompose.ui.theme.BlogComposeTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var loading = true
        val splash = installSplashScreen()
        splash.setKeepOnScreenCondition { loading }
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            BlogComposeTheme {
                ProvideWindowInsets(windowInsetsAnimationsEnabled = true) {
                    Scaffold(modifier = Modifier
                        .navigationBarsWithImePadding()
                        .systemBarsPadding()) {

                        loading=false
                        HomeNavHost { loading = false }

                    }
                }
            }
        }
    }
}
