package com.example.playlistmaker.navigation

import android.content.Intent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.playlistmaker.ui.alltracks.AllTracksActivity
import com.example.playlistmaker.ui.screens.MainScreen
import com.example.playlistmaker.ui.screens.SearchScreen
import com.example.playlistmaker.ui.screens.SettingsScreen

@Composable
fun PlaylistHost(
    navController: NavHostController,
    innerPadding: PaddingValues
) {
    val context = LocalContext.current

    NavHost(
        navController = navController,
        startDestination = PlaylistScreen.MAIN.route
    ) {
        composable(PlaylistScreen.MAIN.route) {
            MainScreen(
                innerPadding = innerPadding,
                onSearchClick = { navigateTo(navController, PlaylistScreen.SEARCH) },
                onLibraryClick = {
                    context.startActivity(Intent(context, AllTracksActivity::class.java))
                },
                onSettingsClick = { navigateTo(navController, PlaylistScreen.SETTINGS) }
            )
        }

        composable(PlaylistScreen.SEARCH.route) {
            SearchScreen(
                innerPadding = innerPadding,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(PlaylistScreen.SETTINGS.route) {
            SettingsScreen(
                innerPadding = innerPadding,
                developerEmail = "pochta_for_yandex@yandex.ru",
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}

private fun navigateTo(
    navController: NavController,
    screen: PlaylistScreen
) {
    navController.navigate(screen.route)
}
