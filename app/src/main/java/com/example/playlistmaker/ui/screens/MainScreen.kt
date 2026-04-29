package com.example.playlistmaker.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.playlistmaker.R
import com.example.playlistmaker.ui.theme.PlaylistMakerTheme

@Composable
fun MainScreen(
    innerPadding: PaddingValues,
    onSongsClick: () -> Unit,
    onPlaylistsClick: () -> Unit,
    onFavoritesClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(horizontal = 20.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(28.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AppMenuRow(
                text = stringResource(R.string.songs_title),
                onClick = onSongsClick
            )
            AppMenuRow(
                text = stringResource(R.string.playlists_title),
                onClick = onPlaylistsClick
            )
            AppMenuRow(
                text = stringResource(R.string.favorites_title),
                onClick = onFavoritesClick
            )
            AppMenuRow(
                text = stringResource(R.string.settings_title),
                onClick = onSettingsClick
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun MainScreenPreview() {
    PlaylistMakerTheme {
        MainScreen(
            innerPadding = PaddingValues(),
            onSongsClick = {},
            onPlaylistsClick = {},
            onFavoritesClick = {},
            onSettingsClick = {}
        )
    }
}
