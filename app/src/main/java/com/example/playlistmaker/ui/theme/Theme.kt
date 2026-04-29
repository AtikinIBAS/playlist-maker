package com.example.playlistmaker.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = AppPrimary,
    onPrimary = AppSurface,
    background = AppBackground,
    onBackground = AppPrimary,
    surface = AppSurface,
    onSurface = AppPrimary,
    secondary = AppSecondary,
    onSecondary = AppSurface,
    outline = AppOutline,
    surfaceVariant = AppSurfaceVariant,
    onSurfaceVariant = AppSecondary,
    tertiary = AppAccent
)

@Composable
fun PlaylistMakerTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
