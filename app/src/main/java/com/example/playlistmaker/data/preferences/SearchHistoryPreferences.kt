package com.example.playlistmaker.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class SearchHistoryPreferences(
    private val dataStore: DataStore<Preferences>,
    private val coroutineScope: CoroutineScope = CoroutineScope(
        CoroutineName("search-history-preferences") + SupervisorJob() + Dispatchers.IO
    )
) {
    fun addEntry(word: String) {
        if (word.isBlank()) {
            return
        }

        val normalizedWord = word.trim()

        coroutineScope.launch {
            dataStore.edit { preferences ->
                val history = preferences[preferencesKey]
                    .orEmpty()
                    .split(SEPARATOR)
                    .filter { it.isNotBlank() }
                    .toMutableList()

                history.removeAll { it.equals(normalizedWord, ignoreCase = true) }
                history.add(0, normalizedWord)

                preferences[preferencesKey] = history
                    .take(MAX_ENTRIES)
                    .joinToString(SEPARATOR)
            }
        }
    }

    suspend fun getEntries(): List<String> {
        return entriesFlow().first()
    }

    fun entriesFlow(): Flow<List<String>> {
        return dataStore.data.map { preferences ->
            preferences[preferencesKey]
                .orEmpty()
                .split(SEPARATOR)
                .filter { it.isNotBlank() }
                .take(MAX_ENTRIES)
        }
    }

    private companion object {
        const val MAX_ENTRIES = 10
        const val SEPARATOR = ","
        val preferencesKey = stringPreferencesKey("search_history_entries")
    }
}
