package com.example.playlistmaker.ui.alltracks

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.viewinterop.AndroidView
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.ui.theme.PlaylistMakerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllTracksScreen(
    modifier: Modifier,
    viewModel: AllTracksViewModel
) {
    val screenState by viewModel.allTracksScreenState.collectAsState()
    var showBottomSheet by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.fetchData()
    }

    Box(modifier = modifier.fillMaxSize()) {
        when (val state = screenState) {
            AllTracksState.Initial -> Unit
            AllTracksState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is AllTracksState.Success -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.foundList) { track ->
                        TrackListItem(track = track)
                        HorizontalDivider(thickness = 0.5.dp)
                    }
                }
            }

            is AllTracksState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.all_tracks_error, state.error),
                        color = Color.Red
                    )
                }
            }
        }

        FloatingActionButton(
            onClick = { showBottomSheet = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = stringResource(R.string.all_tracks_fab_content_description)
            )
        }
    }

    TracksBottomSheet(
        modifier = modifier,
        isShowPanel = showBottomSheet,
        onDismissRequest = { showBottomSheet = false }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TracksBottomSheet(
    modifier: Modifier,
    isShowPanel: Boolean,
    onDismissRequest: () -> Unit
) {
    if (isShowPanel) {
        ModalBottomSheet(onDismissRequest = onDismissRequest) {
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.all_tracks_bottom_sheet_title),
                    modifier = Modifier.fillMaxWidth()
                )
                Icon(
                    modifier = Modifier
                        .size(32.dp)
                        .padding(top = 16.dp),
                    imageVector = Icons.Default.Home,
                    contentDescription = stringResource(R.string.all_tracks_bottom_sheet_icon_description)
                )
            }
        }
    }
}

@Composable
fun TrackListItem(
    track: Track,
    onLongClick: (() -> Unit)? = null,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = { onLongClick?.invoke() }
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TrackArtwork(
            imageUrl = track.image,
            contentDescription = stringResource(R.string.track_item_content_description, track.trackName)
        )
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = track.trackName,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = track.artistName,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Text(
            text = track.trackTime,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun TrackArtwork(
    imageUrl: String,
    contentDescription: String
) {
    Box(
        modifier = Modifier
            .width(60.dp)
            .aspectRatio(2f / 3f)
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

@Preview(showBackground = true)
@Composable
private fun PreviewTrackListItem() {
    PlaylistMakerTheme {
        TrackListItem(
            track = Track(
                trackName = "Владивосток 2000",
                artistName = "Мумий Тролль",
                trackTime = "2:38"
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewTrackScreenContent() {
    PlaylistMakerTheme {
        TrackListItem(
            track = Track(
                trackName = "Владивосток 2000",
                artistName = "Мумий Тролль",
                trackTime = "2:38"
            )
        )
    }
}
