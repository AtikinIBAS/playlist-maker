package com.example.playlistmaker.data.repository

import com.example.playlistmaker.data.storage.DatabaseMock
import com.example.playlistmaker.domain.model.Word
import com.example.playlistmaker.domain.repository.SearchHistoryRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

class SearchHistoryRepositoryImpl(
    scope: CoroutineScope
) : SearchHistoryRepository {
    @Suppress("unused")
    private val repositoryScope = scope
    private val database = DatabaseMock()

    override fun getHistoryRequests(): Flow<List<String>> {
        return database.getHistoryRequests()
    }

    override fun addToHistory(word: String) {
        if (word.isBlank()) return
        database.addToHistory(Word(word = word.trim()))
    }
}
