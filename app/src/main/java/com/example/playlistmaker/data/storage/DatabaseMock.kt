package com.example.playlistmaker.data.storage

import com.example.playlistmaker.domain.model.Word
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class DatabaseMock {
    private val historyList = mutableListOf<Word>()
    private val historyUpdates = MutableStateFlow<List<String>>(emptyList())

    fun getHistoryRequests() = historyUpdates.asStateFlow()

    fun addToHistory(word: Word) {
        val existingIndex = historyList.indexOfFirst { it.word.equals(word.word, ignoreCase = true) }

        if (existingIndex >= 0) {
            val existing = historyList.removeAt(existingIndex)
            historyList.add(0, existing.copy(count = existing.count + 1))
        } else {
            historyList.add(0, word)
        }

        if (historyList.size > MAX_HISTORY_ITEMS) {
            historyList.removeLast()
        }

        historyUpdates.update { historyList.map { item -> item.word } }
    }

    private companion object {
        const val MAX_HISTORY_ITEMS = 10
    }
}
