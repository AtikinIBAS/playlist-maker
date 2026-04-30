package com.example.playlistmaker.ui.screens

import android.widget.ImageView
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.ui.alltracks.TrackListItem
import com.example.playlistmaker.ui.playlist.PlaylistsViewModel
import com.example.playlistmaker.ui.theme.AppPrimary
import com.example.playlistmaker.ui.theme.AppSecondary

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
    val playlists by playlistsViewModel.playlists.collectAsState(emptyList())
    val playlist = playlists.find { it.id == playlistId }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .background(MaterialTheme.colorScheme.surface)
    ) {
        PlaylistDetailsHeader(onBackClick = navigateBack)

        if (playlist == null) {
            EmptyState(
                text = stringResource(R.string.playlist_not_found),
                modifier = Modifier.fillMaxWidth()
            )
            return@Column
        }

        PlaylistDetailsContent(
            playlist = playlist,
            onTrackClick = onTrackClick
        )
    }
}

@Composable
private fun PlaylistDetailsHeader(
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clickable(onClick = onBackClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.back_button_content_description),
                tint = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Composable
private fun PlaylistDetailsContent(
    playlist: Playlist,
    onTrackClick: (Long) -> Unit
) {
    val coverUrl = playlist.imagePath.ifBlank { playlist.tracks.firstOrNull()?.image.orEmpty() }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            PlaylistCoverArt(
                imageUrl = coverUrl,
                contentDescription = playlist.name
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = playlist.name,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            if (playlist.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = playlist.description,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Normal),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = buildPlaylistMeta(playlist),
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Normal),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .clickable { }
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = stringResource(R.string.playlist_more_actions),
                    tint = AppPrimary,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            items(
                items = playlist.tracks,
                key = { track -> track.id }
            ) { track ->
                TrackListItem(
                    track = track,
                    onClick = { onTrackClick(track.id) }
                )
            }
        }
    }
}

@Composable
private fun PlaylistCoverArt(
    imageUrl: String,
    contentDescription: String
) {
    Box(
        modifier = Modifier
            .size(312.dp)
            .clip(RoundedCornerShape(8.dp))
    ) {
        AndroidView(
            factory = { context ->
                ImageView(context).apply {
                    scaleType = ImageView.ScaleType.CENTER_CROP
                    this.contentDescription = contentDescription
                }
            },
            modifier = Modifier.fillMaxSize(),
            update = { imageView ->
                Glide.with(imageView)
                    .load(imageUrl.ifBlank { null })
                    .placeholder(R.drawable.ic_music)
                    .error(R.drawable.ic_music)
                    .into(imageView)
            }
        )
    }
}

private fun buildPlaylistMeta(playlist: Playlist): String {
    val totalSeconds = playlist.tracks.sumOf { track ->
        val parts = track.trackTime.split(":")
        val minutes = parts.getOrNull(0)?.toIntOrNull() ?: 0
        val seconds = parts.getOrNull(1)?.toIntOrNull() ?: 0
        minutes * 60 + seconds
    }
    val totalMinutes = totalSeconds / 60
    return "${formatMinuteCount(totalMinutes)} • ${formatTrackCount(playlist.tracks.size)}"
}

private fun formatMinuteCount(count: Int): String {
    val mod10 = count % 10
    val mod100 = count % 100
    val suffix = when {
        mod10 == 1 && mod100 != 11 -> "минута"
        mod10 in 2..4 && mod100 !in 12..14 -> "минуты"
        else -> "минут"
    }
    return "$count $suffix"
}

private fun formatTrackCount(count: Int): String {
    val mod10 = count % 10
    val mod100 = count % 100
    val suffix = when {
        mod10 == 1 && mod100 != 11 -> "трек"
        mod10 in 2..4 && mod100 !in 12..14 -> "трека"
        else -> "треков"
    }
    return "$count $suffix"
}
