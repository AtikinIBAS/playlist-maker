package com.example.playlistmaker.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.net.Uri
import android.widget.Toast
import android.widget.ImageView
import androidx.activity.compose.rememberLauncherForActivityResult
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.ui.playlist.PlaylistsViewModel
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

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            val savedPath = copyPlaylistImageToInternalStorage(context, uri)
            if (savedPath.isNotBlank()) {
                playlistImagePath = savedPath
            }
        }
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            imagePickerLauncher.launch("image/*")
        } else {
            Toast.makeText(
                context,
                context.getString(R.string.permission_required),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp)
            .navigationBarsPadding()
    ) {
        ScreenHeader(
            title = stringResource(R.string.new_playlist_title),
            onBackClick = navigateBack
        )

        Spacer(modifier = Modifier.height(24.dp))

        PlaylistArtworkPicker(
            imagePath = playlistImagePath,
            onClick = {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    imagePickerLauncher.launch("image/*")
                } else {
                    when {
                        ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        ) == PackageManager.PERMISSION_GRANTED -> {
                            imagePickerLauncher.launch("image/*")
                        }

                        else -> permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                    }
                }
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        AppPlaylistTextField(
            value = playlistName,
            label = stringResource(R.string.new_playlist_name),
            singleLine = true,
            onValueChange = { playlistName = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        AppPlaylistTextField(
            value = playlistDescription,
            label = stringResource(R.string.new_playlist_description),
            singleLine = false,
            minLines = 2,
            onValueChange = { playlistDescription = it }
        )

        Spacer(modifier = Modifier.weight(1f))

        AppActionButton(
            text = stringResource(R.string.create_playlist),
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

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun PlaylistArtworkPicker(
    imagePath: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(208.dp)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (imagePath.isBlank()) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                androidx.compose.material3.Icon(
                    imageVector = Icons.Default.AddPhotoAlternate,
                    contentDescription = stringResource(R.string.playlist_pick_cover),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(72.dp)
                )
            }
        } else {
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
                            .load(imagePath)
                            .into(imageView)
                    }
                )
            }
        }
    }
}

@Composable
private fun AppPlaylistTextField(
    value: String,
    label: String,
    singleLine: Boolean,
    minLines: Int = 1,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        textStyle = MaterialTheme.typography.bodyMedium,
        label = { Text(text = label) },
        singleLine = singleLine,
        minLines = minLines,
        shape = RoundedCornerShape(8.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.tertiary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            focusedLabelColor = MaterialTheme.colorScheme.tertiary,
            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    )
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
