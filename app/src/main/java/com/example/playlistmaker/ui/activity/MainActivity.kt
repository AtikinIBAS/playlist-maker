package com.example.playlistmaker.ui.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.playlistmaker.PlaylistMakerApp
import com.example.playlistmaker.navigation.PlaylistHost
import com.example.playlistmaker.ui.theme.PlaylistMakerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val app = application as PlaylistMakerApp
            val isDarkTheme by app.themePreferences.isDarkThemeFlow.collectAsState(initial = false)

            PlaylistMakerTheme(darkTheme = isDarkTheme) {
                val navController = rememberNavController()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    PlaylistHost(
                        navController = navController,
                        innerPadding = innerPadding,
                        isDarkTheme = isDarkTheme,
                        onThemeToggle = app.themePreferences::setDarkTheme
                    )
                }
            }
        }
    }
}
