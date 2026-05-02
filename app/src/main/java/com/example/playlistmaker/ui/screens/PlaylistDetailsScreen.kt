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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.model.Track
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
    val playlists by playlistsViewModel.playlists.collectAsState(emptyList())
    val playlist = playlists.find { it.id == playlistId }
    var trackToDelete by remember { mutableStateOf<Track?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clickable(onClick = navigateBack),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back_button_content_description),
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        if (playlist == null) {
            EmptyState(
                text = stringResource(R.string.playlist_not_found),
                modifier = Modifier.fillMaxWidth()
            )
            return@Column
        }

        val coverUrl = playlist.imagePath.ifBlank { playlist.tracks.firstOrNull()?.image.orEmpty() }
        val totalSeconds = playlist.tracks.sumOf { track ->
            val parts = track.trackTime.split(":")
            val minutes = parts.getOrNull(0)?.toIntOrNull() ?: 0
            val seconds = parts.getOrNull(1)?.toIntOrNull() ?: 0
            minutes * 60 + seconds
        }
        val totalMinutes = totalSeconds / 60

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            contentAlignment = Alignment.Center
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
                        }
                    },
                    modifier = Modifier.fillMaxSize(),
                    update = { imageView ->
                        Glide.with(imageView)
                            .load(coverUrl.ifBlank { null })
                            .placeholder(R.drawable.ic_music)
                            .error(R.drawable.ic_music)
                            .into(imageView)
                    }
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Text(
                text = playlist.name,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            if (playlist.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = playlist.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${formatMinuteCount(totalMinutes)} • ${formatTrackCount(playlist.tracks.size)}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "⋮",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            items(playlist.tracks, key = { it.id }) { track ->
                TrackListItem(
                    track = track,
                    onLongClick = { trackToDelete = track },
                    onClick = { onTrackClick(track.id) }
                )
            }
        }
    }

    trackToDelete?.let { track ->
        Dialog(onDismissRequest = { trackToDelete = null }) {
            Surface(
                modifier = Modifier.requiredWidth(280.dp),
                shape = RoundedCornerShape(4.dp),
                color = Color.White,
                shadowElevation = 10.dp
            ) {
                Column(
                    modifier = Modifier.padding(start = 24.dp, top = 23.dp, end = 8.dp, bottom = 8.dp)
                ) {
                    Text(
                        text = "Хотите удалить трек?",
                        modifier = Modifier
                            .requiredWidth(248.dp)
                            .requiredHeight(20.dp),
                        color = Color(0xFF1A1B22),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        lineHeight = 20.sp,
                        letterSpacing = 0.25.sp
                    )
                    Row(
                        modifier = Modifier
                            .requiredWidth(248.dp)
                            .requiredHeight(72.dp)
                            .padding(top = 36.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        DialogActionButton(
                            text = "НЕТ",
                            width = 46.dp,
                            onClick = { trackToDelete = null }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        DialogActionButton(
                            text = "ДА",
                            width = 38.dp,
                            onClick = {
                                playlistsViewModel.deleteTrackFromPlaylist(track)
                                trackToDelete = null
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DialogActionButton(
    text: String,
    width: androidx.compose.ui.unit.Dp,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .requiredWidth(width)
            .requiredHeight(36.dp)
            .clip(RoundedCornerShape(4.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color(0xFF3772E7),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            lineHeight = 16.sp,
            letterSpacing = 1.25.sp
        )
    }
}

