package com.example.playlistmaker.ui.alltracks

import androidx.compose.foundation.clickable
import androidx.compose.foundation.Image
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.ui.theme.PlaylistMakerTheme

@Composable
fun AllTracksScreen(
    modifier: Modifier,
    viewModel: AllTracksViewModel
) {
    val screenState by viewModel.allTracksScreenState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchData()
    }

    when (val state = screenState) {
        AllTracksState.Initial -> Unit
        AllTracksState.Loading -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is AllTracksState.Success -> {
            LazyColumn(
                modifier = modifier.fillMaxSize(),
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
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.all_tracks_error, state.error),
                    color = Color.Red
                )
            }
        }
    }
}

@Composable
fun TrackListItem(
    track: Track,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_music),
            contentDescription = stringResource(R.string.track_item_content_description, track.trackName),
            modifier = Modifier.size(48.dp)
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
