package com.example.playlistmaker.ui.alltracks

import com.example.playlistmaker.domain.model.Track

sealed class AllTracksState {
    data object Initial : AllTracksState()
    data object Loading : AllTracksState()
    data class Success(val foundList: List<Track>) : AllTracksState()
    data class Error(val error: String) : AllTracksState()
}
