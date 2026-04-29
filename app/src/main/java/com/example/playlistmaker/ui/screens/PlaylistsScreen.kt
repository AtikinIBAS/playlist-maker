package com.example.playlistmaker.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.ui.playlist.PlaylistsViewModel

@Composable
fun PlaylistsScreen(
    innerPadding: PaddingValues,
    playlistsViewModel: PlaylistsViewModel,
    addNewPlaylist: () -> Unit,
    navigateToPlaylist: (Long) -> Unit,
    navigateBack: () -> Unit
) {
    val playlists by playlistsViewModel.playlists.collectAsState(emptyList())

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            ScreenHeader(
                title = stringResource(R.string.playlists_title),
                onBackClick = navigateBack
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(playlists) { playlist ->
                    PlaylistListItem(
                        playlist = playlist,
                        onClick = { navigateToPlaylist(playlist.id) }
                    )
                }
            }
        }

        FloatingActionButton(
            modifier = Modifier
                .padding(24.dp)
                .align(Alignment.BottomEnd),
            onClick = addNewPlaylist
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = stringResource(R.string.add_playlist)
            )
        }
    }
}

@Composable
fun PlaylistListItem(
    playlist: Playlist,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        tonalElevation = 1.dp,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Image(
                modifier = Modifier.size(48.dp),
                painter = painterResource(id = R.drawable.ic_music),
                contentDescription = playlist.name,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurfaceVariant)
            )
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = playlist.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = stringResource(R.string.playlist_tracks_count, playlist.tracks.size),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            ChevronMark()
        }
    }
}
