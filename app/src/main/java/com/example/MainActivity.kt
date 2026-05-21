package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.viewmodel.FreshNovaViewModel
import com.example.ui.viewmodel.Screen

class MainActivity : ComponentActivity() {

    // Retrieve central ViewModel using Jetpack viewModels delegate
    private val viewModel: FreshNovaViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Setup modern immersive Edge-to-Edge layout
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val currentScreen by viewModel.currentScreen.collectAsState()

                    // Premium screen transitions
                    Crossfade(
                        targetState = currentScreen,
                        label = "ActivityScreenTransition"
                    ) { screen ->
                        when (screen) {
                            is Screen.Splash -> {
                                SplashScreen()
                            }
                            is Screen.Login -> {
                                LoginScreen(
                                    onLoginSuccess = { email, pass ->
                                        viewModel.login(email, pass)
                                    }
                                )
                            }
                            is Screen.Dashboard -> {
                                DashboardScreen(viewModel = viewModel)
                            }
                        }
                    }
                }
            }
        }
    }
}
