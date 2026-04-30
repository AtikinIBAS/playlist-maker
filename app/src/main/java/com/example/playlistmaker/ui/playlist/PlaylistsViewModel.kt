package com.example.playlistmaker.ui.playlist

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
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PlaylistsViewModel(
    private val playlistsRepository: PlaylistsRepository,
    private val tracksRepository: TracksRepository
) : ViewModel() {
    val playlists: StateFlow<List<Playlist>> = playlistsRepository.getAllPlaylists().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = emptyList()
    )

    val favoriteList: StateFlow<List<Track>> = tracksRepository.getFavoriteTracks().stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = emptyList()
    )

    fun createNewPlayList(namePlaylist: String, description: String, imagePath: String) {
        viewModelScope.launch(Dispatchers.IO) {
            playlistsRepository.addNewPlaylist(namePlaylist, description, imagePath)
        }
    }

    fun insertTrackToPlaylist(track: Track, playlistId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            tracksRepository.insertTrackToPlaylist(track, playlistId)
        }
    }

    fun toggleFavorite(track: Track, isFavorite: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            tracksRepository.updateTrackFavoriteStatus(track, isFavorite)
        }
    }

    fun deleteTrackFromPlaylist(track: Track) {
        viewModelScope.launch(Dispatchers.IO) {
            tracksRepository.deleteTrackFromPlaylist(track)
        }
    }

    fun deletePlaylistById(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            tracksRepository.deleteTracksByPlaylistId(id)
            playlistsRepository.deletePlaylistById(id)
        }
    }

    suspend fun isExist(track: Track): Track? {
        return tracksRepository.getTrackByNameAndArtist(track).firstOrNull()
    }

    companion object {
        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                PlaylistsViewModel(
                    playlistsRepository = Creator.getPlaylistsRepository(),
                    tracksRepository = Creator.getTracksRepository()
                )
            }
        }
    }
}
