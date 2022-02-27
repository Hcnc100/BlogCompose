package com.nullpointer.blogcompose.ui.activitys

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.Scaffold
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.navigationBarsWithImePadding
import com.nullpointer.blogcompose.ui.screens.authScreen.AuthScreen
import com.nullpointer.blogcompose.ui.screens.dataUser.DataUserScreen
import com.nullpointer.blogcompose.ui.theme.BlogComposeTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            BlogComposeTheme {
                ProvideWindowInsets(windowInsetsAnimationsEnabled = true) {
                    Scaffold(modifier = Modifier
                        .navigationBarsWithImePadding()
                        .systemBarsPadding()) {
//                        AddBlogScreen()
//                        AuthScreen()
                        DataUserScreen()
                    }
                }
            }
        }
    }
}
