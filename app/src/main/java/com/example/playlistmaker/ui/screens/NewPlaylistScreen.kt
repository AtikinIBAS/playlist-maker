package com.example.playlistmaker.ui.screens

import android.content.Context
import android.net.Uri
import android.widget.ImageView
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.ui.playlist.PlaylistsViewModel
import com.example.playlistmaker.ui.theme.AppPrimary
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

@Composable
fun NewPlaylistScreen(
    innerPadding: PaddingValues,
    playlistsViewModel: PlaylistsViewModel,
    navigateBack: () -> Unit
) {
    val context = LocalContext.current
    var playlistName by rememberSaveable { mutableStateOf("") }
    var playlistDescription by rememberSaveable { mutableStateOf("") }
    var playlistImagePath by rememberSaveable { mutableStateOf("") }
    var selectedImageUri by rememberSaveable { mutableStateOf<String?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            val savedPath = copyPlaylistImageToInternalStorage(context, uri)
            if (savedPath.isNotBlank()) {
                playlistImagePath = savedPath
                selectedImageUri = savedPath
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .background(Color.White)
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Top
    ) {
        ScreenHeader(
            title = stringResource(R.string.new_playlist_title),
            onBackClick = navigateBack
        )

        Spacer(modifier = Modifier.height(18.dp))

        PlaylistArtworkPicker(
            imagePath = selectedImageUri,
            onClick = {
                imagePickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = playlistName,
            onValueChange = { playlistName = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.new_playlist_name)) },
            singleLine = true,
            shape = RoundedCornerShape(8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = playlistDescription,
            onValueChange = { playlistDescription = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text(stringResource(R.string.new_playlist_description)) },
            minLines = 4,
            shape = RoundedCornerShape(8.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        AppActionButton(
            text = stringResource(R.string.save_playlist),
            enabled = playlistName.isNotBlank(),
            onClick = {
                playlistsViewModel.createNewPlayList(
                    namePlaylist = playlistName.trim(),
                    description = playlistDescription.trim(),
                    imagePath = playlistImagePath
                )
                navigateBack()
            }
        )
    }
}

@Composable
private fun PlaylistArtworkPicker(
    imagePath: String?,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(312.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFF2F3F5))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (imagePath.isNullOrBlank()) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                androidx.compose.material3.Icon(
                    imageVector = Icons.Default.AddPhotoAlternate,
                    contentDescription = stringResource(R.string.playlist_pick_cover),
                    tint = AppPrimary,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = stringResource(R.string.playlist_pick_cover),
                    color = AppPrimary,
                    fontWeight = FontWeight.Medium
                )
            }
        } else {
            AndroidView(
                factory = { context ->
                    ImageView(context).apply {
                        scaleType = ImageView.ScaleType.CENTER_CROP
                    }
                },
                modifier = Modifier.fillMaxSize(),
                update = { imageView ->
                    Glide.with(imageView)
                        .load(imagePath)
                        .into(imageView)
                }
            )
        }
    }
}

private fun copyPlaylistImageToInternalStorage(context: Context, uri: Uri): String {
    return runCatching {
        val coversDir = File(context.filesDir, "playlist_covers").apply { mkdirs() }
        val targetFile = File(coversDir, "cover_${UUID.randomUUID()}.jpg")
        context.contentResolver.openInputStream(uri)?.use { input ->
            FileOutputStream(targetFile).use { output ->
                input.copyTo(output)
            }
        }
        targetFile.absolutePath
    }.getOrDefault("")
}
