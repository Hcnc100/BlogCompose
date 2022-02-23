package com.nullpointer.blogcompose.ui.activitys

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.nullpointer.blogcompose.ui.screens.homeScreen.HomeScreen
import com.nullpointer.blogcompose.ui.screens.homeScreen.profileScreen.ProfileScreen
import com.nullpointer.blogcompose.ui.theme.BlogComposeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BlogComposeTheme {
//                BlogScreen()
//                SearchScreen()
//                NotifyScreen()
//                ProfileScreen()
                HomeScreen()
            }
        }
    }
}
