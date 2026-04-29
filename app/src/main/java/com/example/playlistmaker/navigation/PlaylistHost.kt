package com.example.playlistmaker.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.playlistmaker.ui.playlist.PlaylistsViewModel
import com.example.playlistmaker.ui.screens.FavoritesScreen
import com.example.playlistmaker.ui.screens.MainScreen
import com.example.playlistmaker.ui.screens.NewPlaylistScreen
import com.example.playlistmaker.ui.screens.PlaylistDetailsScreen
import com.example.playlistmaker.ui.screens.PlaylistsScreen
import com.example.playlistmaker.ui.screens.SearchScreen
import com.example.playlistmaker.ui.screens.SettingsScreen
import com.example.playlistmaker.ui.screens.TrackDetailsScreen
import com.example.playlistmaker.ui.search.SearchViewModel

@Composable
fun PlaylistHost(
    navController: NavHostController,
    innerPadding: PaddingValues
) {
    NavHost(
        navController = navController,
        startDestination = PlaylistScreen.MAIN.route
    ) {
        composable(PlaylistScreen.MAIN.route) {
            MainScreen(
                innerPadding = innerPadding,
                onSongsClick = { navigateTo(navController, PlaylistScreen.SONGS) },
                onPlaylistsClick = { navigateTo(navController, PlaylistScreen.PLAYLISTS) },
                onFavoritesClick = { navigateTo(navController, PlaylistScreen.FAVORITES) },
                onSettingsClick = { navigateTo(navController, PlaylistScreen.SETTINGS) }
            )
        }

        composable(PlaylistScreen.SONGS.route) {
            val searchViewModel = viewModel<SearchViewModel>(
                factory = SearchViewModel.getViewModelFactory()
            )

            SearchScreen(
                innerPadding = innerPadding,
                viewModel = searchViewModel,
                onBackClick = { navController.popBackStack() },
                onTrackClick = { track ->
                    navController.navigate(PlaylistScreen.TRACK_DETAILS.routeWithArg(track.id))
                }
            )
        }

        composable(PlaylistScreen.PLAYLISTS.route) {
            val playlistsViewModel = viewModel<PlaylistsViewModel>(
                factory = PlaylistsViewModel.getViewModelFactory()
            )

            PlaylistsScreen(
                innerPadding = innerPadding,
                playlistsViewModel = playlistsViewModel,
                addNewPlaylist = { navigateTo(navController, PlaylistScreen.NEW_PLAYLIST) },
                navigateToPlaylist = { playlistId ->
                    navController.navigate(PlaylistScreen.PLAYLIST_DETAILS.routeWithArg(playlistId))
                },
                navigateBack = { navController.popBackStack() }
            )
        }

        composable(PlaylistScreen.NEW_PLAYLIST.route) {
            val playlistsViewModel = viewModel<PlaylistsViewModel>(
                factory = PlaylistsViewModel.getViewModelFactory()
            )

            NewPlaylistScreen(
                innerPadding = innerPadding,
                playlistsViewModel = playlistsViewModel,
                navigateBack = { navController.popBackStack() }
            )
        }

        composable(PlaylistScreen.FAVORITES.route) {
            val playlistsViewModel = viewModel<PlaylistsViewModel>(
                factory = PlaylistsViewModel.getViewModelFactory()
            )

            FavoritesScreen(
                innerPadding = innerPadding,
                playlistsViewModel = playlistsViewModel,
                navigateBack = { navController.popBackStack() },
                onTrackClick = { track ->
                    navController.navigate(PlaylistScreen.TRACK_DETAILS.routeWithArg(track.id))
                }
            )
        }

        composable(
            route = "${PlaylistScreen.PLAYLIST_DETAILS.route}/{trackId}",
            arguments = listOf(navArgument("trackId") { type = NavType.LongType })
        ) { backStackEntry ->
            val playlistId = backStackEntry.arguments?.getLong("trackId") ?: 0L
            PlaylistDetailsScreen(
                innerPadding = innerPadding,
                playlistId = playlistId,
                navigateBack = { navController.popBackStack() },
                onTrackClick = { trackId ->
                    navController.navigate(PlaylistScreen.TRACK_DETAILS.routeWithArg(trackId))
                }
            )
        }

        composable(
            route = "${PlaylistScreen.TRACK_DETAILS.route}/{trackId}",
            arguments = listOf(navArgument("trackId") { type = NavType.LongType })
        ) { backStackEntry ->
            val trackId = backStackEntry.arguments?.getLong("trackId") ?: 0L
            TrackDetailsScreen(
                innerPadding = innerPadding,
                trackId = trackId,
                navigateBack = { navController.popBackStack() }
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
