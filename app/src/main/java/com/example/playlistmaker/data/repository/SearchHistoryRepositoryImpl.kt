package com.example.playlistmaker.data.repository

import com.example.playlistmaker.data.preferences.SearchHistoryPreferences
import com.example.playlistmaker.domain.repository.SearchHistoryRepository
import kotlinx.coroutines.flow.Flow

class SearchHistoryRepositoryImpl(
    private val preferences: SearchHistoryPreferences,
) : SearchHistoryRepository {
    override fun getHistoryRequests(): Flow<List<String>> {
        return preferences.entriesFlow()
    }

    override fun addToHistory(word: String) {
        preferences.addEntry(word)
    }
}
