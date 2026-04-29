package com.example.playlistmaker.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.ui.alltracks.TrackListItem
import com.example.playlistmaker.ui.playlist.PlaylistsViewModel

@Composable
fun FavoritesScreen(
    innerPadding: PaddingValues,
    playlistsViewModel: PlaylistsViewModel,
    navigateBack: () -> Unit,
    onTrackClick: (Track) -> Unit
) {
    val favoriteList by playlistsViewModel.favoriteList.collectAsState(emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(horizontal = 20.dp, vertical = 24.dp)
    ) {
        ScreenHeader(
            title = stringResource(R.string.favorites_title),
            onBackClick = navigateBack
        )

        if (favoriteList.isEmpty()) {
            EmptyState(
                text = stringResource(R.string.favorites_empty),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(favoriteList) { track ->
                    TrackListItem(
                        track = track,
                        onLongClick = { playlistsViewModel.toggleFavorite(track, false) },
                        onClick = { onTrackClick(track) }
                    )
                }
            }
        }
    }
}
