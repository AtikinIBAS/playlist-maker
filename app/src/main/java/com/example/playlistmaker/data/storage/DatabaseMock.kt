package com.example.playlistmaker.data.storage

import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.model.Word
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class DatabaseMock(
    seedTracks: List<Track>
) {
    private val historyList = mutableListOf<Word>()
    private val historyUpdates = MutableStateFlow<List<String>>(emptyList())
    private val playlists = MutableStateFlow<List<Playlist>>(emptyList())
    private val tracks = MutableStateFlow(seedTracks)

    fun getHistoryRequests(): Flow<List<String>> = historyUpdates.asStateFlow()

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

    fun getAllPlaylists(): Flow<List<Playlist>> {
        return combine(playlists, tracks) { currentPlaylists, currentTracks ->
            currentPlaylists.map { playlist ->
                playlist.copy(
                    tracks = currentTracks.filter { track -> track.playlistId == playlist.id }
                )
            }
        }
    }

    fun getPlaylist(id: Long): Flow<Playlist?> {
        return getAllPlaylists().map { list -> list.find { it.id == id } }
    }

    fun addNewPlaylist(name: String, description: String) {
        playlists.update { current ->
            current + Playlist(
                id = (current.maxOfOrNull { it.id } ?: 0) + 1,
                name = name,
                description = description,
                tracks = emptyList()
            )
        }
    }

    fun deletePlaylistById(playlistId: Long) {
        playlists.update { current -> current.filterNot { it.id == playlistId } }
    }

    fun deleteTracksByPlaylistId(playlistId: Long) {
        tracks.update { current ->
            current.map { track ->
                if (track.playlistId == playlistId) track.copy(playlistId = 0) else track
            }
        }
    }

    fun deleteTrackFromPlaylist(trackId: Long) {
        tracks.update { current ->
            current.map { track ->
                if (track.id == trackId) track.copy(playlistId = 0) else track
            }
        }
    }

    fun getTrackByNameAndArtist(track: Track): Flow<Track?> {
        return tracks.map { current ->
            current.find { it.trackName == track.trackName && it.artistName == track.artistName }
        }
    }

    fun getTrackById(trackId: Long): Flow<Track?> {
        return tracks.map { current -> current.find { it.id == trackId } }
    }

    fun insertTrack(track: Track) {
        tracks.update { current ->
            current.map { existing ->
                if (existing.id == track.id) track else existing
            }
        }
    }

    fun getFavoriteTracks(): Flow<List<Track>> {
        return tracks.map { current -> current.filter { it.favorite } }
    }

    fun searchTracks(expression: String): List<Track> {
        if (expression.isBlank()) return tracks.value
        return tracks.value.filter {
            it.trackName.contains(expression, ignoreCase = true) ||
                it.artistName.contains(expression, ignoreCase = true)
        }
    }

    fun getAllTracks(): List<Track> = tracks.value

    private companion object {
        const val MAX_HISTORY_ITEMS = 10
    }
}
