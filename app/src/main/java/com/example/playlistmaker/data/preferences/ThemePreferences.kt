package com.example.playlistmaker.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class ThemePreferences(
    private val dataStore: DataStore<Preferences>,
    private val coroutineScope: CoroutineScope = CoroutineScope(
        CoroutineName("theme-preferences") + SupervisorJob() + Dispatchers.IO
    )
) {
    val isDarkThemeFlow: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[darkThemeKey] ?: false
    }

    fun setDarkTheme(enabled: Boolean) {
        coroutineScope.launch {
            dataStore.edit { preferences ->
                preferences[darkThemeKey] = enabled
            }
        }
    }

    private companion object {
        val darkThemeKey = booleanPreferencesKey("dark_theme_enabled")
    }
}
