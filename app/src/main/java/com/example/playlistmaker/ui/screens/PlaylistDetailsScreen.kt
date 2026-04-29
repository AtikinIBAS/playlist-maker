package com.example.playlistmaker.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.playlistmaker.R
import com.example.playlistmaker.ui.alltracks.TrackListItem
import com.example.playlistmaker.ui.playlist.PlaylistsViewModel

@Composable
fun PlaylistDetailsScreen(
    innerPadding: PaddingValues,
    playlistId: Long,
    navigateBack: () -> Unit,
    onTrackClick: (Long) -> Unit,
    playlistsViewModel: PlaylistsViewModel = viewModel(
        factory = PlaylistsViewModel.getViewModelFactory()
    )
) {
    val playlists by playlistsViewModel.playlists.collectAsState()
    val playlist = playlists.find { it.id == playlistId }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(16.dp)
    ) {
        ScreenHeader(
            title = playlist?.name ?: stringResource(R.string.playlist_details_title),
            onBackClick = navigateBack
        )

        if (playlist == null) {
            Text(
                text = stringResource(R.string.playlist_not_found),
                modifier = Modifier.padding(top = 24.dp)
            )
            return@Column
        }

        if (playlist.description.isNotBlank()) {
            Text(
                text = playlist.description,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 12.dp)
            )
        }

        Text(
            text = stringResource(R.string.playlist_tracks_count, playlist.tracks.size),
            modifier = Modifier.padding(top = 12.dp)
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp)
        ) {
            items(playlist.tracks) { track ->
                TrackListItem(
                    track = track,
                    onClick = { onTrackClick(track.id) }
                )
                HorizontalDivider(thickness = 0.5.dp)
            }
        }
    }
}
