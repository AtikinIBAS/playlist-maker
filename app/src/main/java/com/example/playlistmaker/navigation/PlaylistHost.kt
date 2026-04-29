package com.example.playlistmaker.navigation

import android.widget.Toast
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.playlistmaker.R
import com.example.playlistmaker.ui.screens.MainScreen
import com.example.playlistmaker.ui.screens.SearchScreen
import com.example.playlistmaker.ui.screens.SettingsScreen

@Composable
fun PlaylistHost(
    navController: NavHostController,
    innerPadding: PaddingValues
) {
    val context = LocalContext.current
    val libraryTitle = stringResource(R.string.media_library_title)

    NavHost(
        navController = navController,
        startDestination = PlaylistScreen.MAIN.route
    ) {
        composable(PlaylistScreen.MAIN.route) {
            MainScreen(
                innerPadding = innerPadding,
                onSearchClick = { navigateTo(navController, PlaylistScreen.SEARCH) },
                onLibraryClick = {
                    Toast.makeText(
                        context,
                        context.getString(R.string.button_clicked_message, libraryTitle),
                        Toast.LENGTH_SHORT
                    ).show()
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
