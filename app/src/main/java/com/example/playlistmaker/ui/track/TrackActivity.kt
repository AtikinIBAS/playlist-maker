package com.example.playlistmaker.ui.track

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.playlistmaker.R
import com.example.playlistmaker.ui.theme.PlaylistMakerTheme

class TrackActivity : ComponentActivity() {

    private val viewModel by viewModels<TrackViewModel> {
        TrackViewModel.getViewModelFactory("123")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            PlaylistMakerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    TrackScreen(
                        modifier = Modifier.padding(innerPadding),
                        previewText = viewModel.getTrackPreview()
                    )
                }
            }
        }
    }
}

@Composable
private fun TrackScreen(
    modifier: Modifier = Modifier,
    previewText: String
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.track_title),
            style = MaterialTheme.typography.headlineSmall
        )
        Text(
            text = previewText,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun TrackScreenPreview() {
    PlaylistMakerTheme {
        TrackScreen(previewText = "Track details for 123")
    }
}
