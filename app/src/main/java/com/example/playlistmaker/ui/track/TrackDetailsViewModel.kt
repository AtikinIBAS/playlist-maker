package com.example.playlistmaker.ui.track

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.repository.PlaylistsRepository
import com.example.playlistmaker.domain.repository.TracksRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TrackDetailsViewModel(
    trackId: Long,
    private val tracksRepository: TracksRepository,
    playlistsRepository: PlaylistsRepository
) : ViewModel() {
    val track: StateFlow<Track?> = tracksRepository.getTrackById(trackId).stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = null
    )

    val playlists: StateFlow<List<Playlist>> = playlistsRepository.getAllPlaylists().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = emptyList()
    )

    fun toggleFavorite() {
        val currentTrack = track.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            tracksRepository.updateTrackFavoriteStatus(currentTrack, !currentTrack.favorite)
        }
    }

    fun addToPlaylist(playlistId: Long) {
        val currentTrack = track.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            tracksRepository.insertTrackToPlaylist(currentTrack, playlistId)
        }
    }

    companion object {
        fun getViewModelFactory(trackId: Long): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                TrackDetailsViewModel(
                    trackId = trackId,
                    tracksRepository = Creator.getTracksRepository(),
                    playlistsRepository = Creator.getPlaylistsRepository()
                )
            }
        }
    }
}
