package com.example.playlistmaker.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = AppDarkPrimary,
    onPrimary = AppDarkBackground,
    background = AppDarkBackground,
    onBackground = AppDarkPrimary,
    surface = AppDarkSurface,
    onSurface = AppDarkPrimary,
    secondary = AppDarkSecondary,
    onSecondary = AppDarkBackground,
    outline = AppDarkOutline,
    surfaceVariant = AppDarkSurfaceVariant,
    onSurfaceVariant = AppDarkSecondary,
    tertiary = AppAccent
)

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
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content
    )
}
