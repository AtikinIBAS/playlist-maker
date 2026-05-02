package com.example.playlistmaker.domain.repository

import kotlinx.coroutines.flow.Flow

interface SearchHistoryRepository {
    fun getHistoryRequests(): Flow<List<String>>

    fun addToHistory(word: String)
}
