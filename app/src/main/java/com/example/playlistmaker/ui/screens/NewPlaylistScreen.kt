package com.example.playlistmaker.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.playlistmaker.R
import com.example.playlistmaker.ui.playlist.PlaylistsViewModel

@Composable
fun NewPlaylistScreen(
    innerPadding: PaddingValues,
    playlistsViewModel: PlaylistsViewModel,
    navigateBack: () -> Unit
) {
    var playlistName by rememberSaveable { mutableStateOf("") }
    var playlistDescription by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        ScreenHeader(
            title = stringResource(R.string.new_playlist_title),
            onBackClick = navigateBack
        )

        OutlinedTextField(
            value = playlistName,
            onValueChange = { playlistName = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.new_playlist_name)) },
            singleLine = true,
            shape = RoundedCornerShape(14.dp)
        )

        OutlinedTextField(
            value = playlistDescription,
            onValueChange = { playlistDescription = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.new_playlist_description)) },
            minLines = 4,
            shape = RoundedCornerShape(14.dp)
        )

        AppActionButton(
            text = stringResource(R.string.save_playlist),
            enabled = playlistName.isNotBlank(),
            onClick = {
                playlistsViewModel.createNewPlayList(
                    namePlaylist = playlistName.trim(),
                    description = playlistDescription.trim()
                )
                navigateBack()
            }
        )
    }
}
