package com.example.playlistmaker.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material3.MaterialTheme
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
            .background(MaterialTheme.colorScheme.surface)
    ) {
        ScreenHeader(
            title = stringResource(R.string.favorites_title),
            onBackClick = navigateBack
        )

        if (favoriteList.isEmpty()) {
            FavoritesEmptyState(
                text = stringResource(R.string.favorites_empty),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 120.dp)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 4.dp),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                items(favoriteList, key = { it.id }) { track ->
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
