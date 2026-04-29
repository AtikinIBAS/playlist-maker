package com.example.playlistmaker.data.repository

import com.example.playlistmaker.data.storage.DatabaseMock
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.repository.TracksRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow

class TracksRepositoryImpl(
    private val database: DatabaseMock
) : TracksRepository {
    override fun getTrackDetails(trackId: String): String {
        return database.getAllTracks().find { it.id.toString() == trackId }?.trackName
            ?: "Track details for $trackId"
    }

    override suspend fun getAllTracks(): List<Track> {
        delay(300)
        return database.getAllTracks()
    }

    override suspend fun searchTracks(expression: String): List<Track> {
        delay(1000)
        return database.searchTracks(expression)
    }

    override fun getTrackById(trackId: Long): Flow<Track?> {
        return database.getTrackById(trackId)
    }

    override fun getTrackByNameAndArtist(track: Track): Flow<Track?> {
        return database.getTrackByNameAndArtist(track)
    }

    override fun getFavoriteTracks(): Flow<List<Track>> {
        return database.getFavoriteTracks()
    }

    override suspend fun insertTrackToPlaylist(track: Track, playlistId: Long) {
        database.insertTrack(track.copy(playlistId = playlistId))
    }

    override suspend fun deleteTrackFromPlaylist(track: Track) {
        database.deleteTrackFromPlaylist(track.id)
    }

    override suspend fun updateTrackFavoriteStatus(track: Track, isFavorite: Boolean) {
        database.insertTrack(track.copy(favorite = isFavorite))
    }

    override suspend fun deleteTracksByPlaylistId(playlistId: Long) {
        database.deleteTracksByPlaylistId(playlistId)
    }
}
