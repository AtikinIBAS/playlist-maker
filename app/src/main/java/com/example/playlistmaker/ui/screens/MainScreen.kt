package com.example.playlistmaker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.LibraryMusic
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.playlistmaker.R
import com.example.playlistmaker.ui.theme.AppAccent
import com.example.playlistmaker.ui.theme.PlaylistMakerTheme

@Composable
fun MainScreen(
    innerPadding: PaddingValues,
    isDarkTheme: Boolean,
    onSongsClick: () -> Unit,
    onPlaylistsClick: () -> Unit,
    onFavoritesClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(164.dp)
                .background(AppAccent)
        )

        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier
                .statusBarsPadding()
                .padding(start = 16.dp, top = 12.dp)
        )

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxSize()
                .padding(top = 70.dp),
            color = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                MainMenuRow(
                    icon = Icons.Filled.Search,
                    text = stringResource(R.string.search_title),
                    onClick = onSongsClick
                )
                MainMenuRow(
                    icon = Icons.Outlined.LibraryMusic,
                    text = stringResource(R.string.playlists_title),
                    onClick = onPlaylistsClick
                )
                MainMenuRow(
                    icon = Icons.Filled.FavoriteBorder,
                    text = stringResource(R.string.favorites_title),
                    onClick = onFavoritesClick
                )
                MainMenuRow(
                    icon = Icons.Filled.Settings,
                    text = stringResource(R.string.settings_title),
                    onClick = onSettingsClick
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun MainScreenLightPreview() {
    PlaylistMakerTheme(darkTheme = false) {
        MainScreen(
            innerPadding = PaddingValues(),
            isDarkTheme = false,
            onSongsClick = {},
            onPlaylistsClick = {},
            onFavoritesClick = {},
            onSettingsClick = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun MainScreenDarkPreview() {
    PlaylistMakerTheme(darkTheme = true) {
        MainScreen(
            innerPadding = PaddingValues(),
            isDarkTheme = true,
            onSongsClick = {},
            onPlaylistsClick = {},
            onFavoritesClick = {},
            onSettingsClick = {}
        )
    }
}
