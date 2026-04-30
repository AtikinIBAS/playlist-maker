package com.example.playlistmaker.ui.screens

import android.widget.ImageView
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
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
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.ui.theme.AppPrimary
import com.example.playlistmaker.ui.theme.AppSecondary
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
                .padding(bottom = 24.dp)
        ) {
            ScreenHeader(
                title = stringResource(R.string.playlists_title),
                onBackClick = navigateBack
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(0.dp)
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
            onClick = addNewPlaylist,
            shape = CircleShape,
            containerColor = AppPrimary.copy(alpha = 0.25f),
            contentColor = Color.White,
            elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 0.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = stringResource(R.string.add_playlist),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun PlaylistListItem(
    playlist: Playlist,
    onClick: () -> Unit,
    showChevron: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(start = 13.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        PlaylistImage(
            imagePath = playlist.imagePath,
            contentDescription = playlist.name
        )
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = playlist.name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = formatTrackCount(playlist.tracks.size),
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Normal),
                color = AppSecondary
            )
        }
        if (showChevron) {
            ChevronMark(
                modifier = Modifier.padding(end = 4.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun PlaylistImage(
    imagePath: String,
    contentDescription: String
) {
    Box(
        modifier = Modifier
            .size(45.dp)
            .clip(RoundedCornerShape(2.dp))
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
                    .load(imagePath.ifBlank { null })
                    .placeholder(R.drawable.ic_music)
                    .error(R.drawable.ic_music)
                    .into(imageView)
            }
        )
    }
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
