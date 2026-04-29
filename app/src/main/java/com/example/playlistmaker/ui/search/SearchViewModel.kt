package com.example.playlistmaker.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.data.repository.SearchHistoryRepositoryImpl
import com.example.playlistmaker.domain.repository.TracksRepository
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(FlowPreview::class)
class SearchViewModel(
    private val tracksRepository: TracksRepository
) : ViewModel() {
    private val searchHistoryRepository = SearchHistoryRepositoryImpl(scope = viewModelScope)
    private val searchQuery = MutableStateFlow("")
    private val _searchScreenState = MutableStateFlow<SearchState>(SearchState.Initial)
    val searchScreenState = _searchScreenState.asStateFlow()
    val historyList = searchHistoryRepository.getHistoryRequests().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = emptyList()
    )

    init {
        viewModelScope.launch {
            searchQuery
                .debounce(1000)
                .distinctUntilChanged()
                .collect { query ->
                    if (query.isBlank()) {
                        _searchScreenState.update { SearchState.Initial }
                    } else {
                        performSearch(query.trim())
                    }
                }
        }
    }

    fun updateQuery(query: String) {
        searchQuery.value = query
    }

    fun clearSearch() {
        searchQuery.value = ""
        _searchScreenState.update { SearchState.Initial }
    }

    private suspend fun performSearch(request: String) {
        try {
            _searchScreenState.update { SearchState.Searching }
            val list = withContext(Dispatchers.IO) {
                searchHistoryRepository.addToHistory(request)
                tracksRepository.searchTracks(expression = request)
            }
            _searchScreenState.update { SearchState.Success(list = list) }
        } catch (e: IOException) {
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
