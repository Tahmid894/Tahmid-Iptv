package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.ui.navigation.Screen
import com.example.ui.screens.ChannelListScreen
import com.example.ui.screens.CountriesScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LanguagesScreen
import com.example.ui.screens.PlayerScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.IptvViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: IptvViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = DarkBackground,
                    contentWindowInsets = WindowInsets(0, 0, 0, 0)
                ) { _ ->
                    AppNavigation(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun AppNavigation(viewModel: IptvViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()

    // Handle back button on sub-screens
    BackHandler(enabled = currentScreen !is Screen.Home) {
        viewModel.navigateBack()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        when (val screen = currentScreen) {
            is Screen.Home -> {
                HomeScreen(viewModel = viewModel)
            }

            is Screen.ChannelList -> {
                ChannelListScreen(
                    screenData = screen,
                    viewModel = viewModel
                )
            }

            is Screen.Countries -> {
                CountriesScreen(viewModel = viewModel)
            }

            is Screen.Languages -> {
                LanguagesScreen(viewModel = viewModel)
            }

            is Screen.Player -> {
                PlayerScreen(
                    screenData = screen,
                    viewModel = viewModel
                )
            }

            is Screen.Settings -> {
                SettingsScreen(viewModel = viewModel)
            }
        }
    }
}
