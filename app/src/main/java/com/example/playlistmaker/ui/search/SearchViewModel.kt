package com.example.playlistmaker.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.domain.repository.TracksRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchViewModel(
    private val tracksRepository: TracksRepository
) : ViewModel() {
    private val searchHistoryRepository = Creator.getSearchHistoryRepository()
    private val searchQuery = MutableStateFlow("")
    private val _searchScreenState = MutableStateFlow<SearchState>(SearchState.Initial)
    private var lastFailedQuery: String? = null

    val searchScreenState = _searchScreenState.asStateFlow()
    val historyList = searchHistoryRepository.getHistoryRequests().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = emptyList()
    )

    fun updateQuery(query: String) {
        searchQuery.value = query
    }

    fun searchNow(query: String = searchQuery.value) {
        val normalizedQuery = query.trim()
        searchQuery.value = query
        if (normalizedQuery.isBlank()) {
            lastFailedQuery = null
            _searchScreenState.update { SearchState.Initial }
            return
        }
        viewModelScope.launch {
            performSearch(normalizedQuery)
        }
    }

    fun clearSearch() {
        lastFailedQuery = null
        searchQuery.value = ""
        _searchScreenState.update { SearchState.Initial }
    }

    fun retrySearch() {
        val failedQuery = lastFailedQuery ?: return
        viewModelScope.launch {
            performSearch(failedQuery)
        }
    }

    private suspend fun performSearch(request: String) {
        try {
            _searchScreenState.update { SearchState.Searching }
            val list = withContext(Dispatchers.IO) {
                searchHistoryRepository.addToHistory(request)
                tracksRepository.searchTracks(expression = request)
            }
            lastFailedQuery = null
            _searchScreenState.update { SearchState.Success(list = list) }
        } catch (e: Exception) {
            lastFailedQuery = request
            _searchScreenState.update { SearchState.Fail(error = e.message.toString()) }
        }
    }

    companion object {
        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SearchViewModel(Creator.getTracksRepository())
            }
        }
    }
}
