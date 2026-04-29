package com.example.playlistmaker.data.repository

import com.example.playlistmaker.data.network.ITunesApiService
import com.example.playlistmaker.data.storage.DatabaseMock
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.repository.TracksRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import java.io.IOException

class TracksRepositoryImpl(
    private val database: DatabaseMock,
    private val iTunesApiService: ITunesApiService
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
        val response = iTunesApiService.searchTracks(expression = expression)

        if (!response.isSuccessful) {
            throw IOException(response.code().toString())
        }

        val tracks = response.body()
            ?.results
            .orEmpty()
            .map { dto ->
                Track(
                    id = dto.trackId ?: generateFallbackId(dto.trackName, dto.artistName),
                    trackName = dto.trackName.orEmpty().ifBlank { "Unknown track" },
                    artistName = dto.artistName.orEmpty().ifBlank { "Unknown artist" },
                    trackTime = formatTrackTime(dto.trackTimeMillis),
                    image = dto.artworkUrl100.orEmpty()
                )
            }

        tracks.forEach { track ->
            val existing = database.getAllTracks().find { it.id == track.id }
            val merged = if (existing != null) {
                track.copy(
                    favorite = existing.favorite,
                    playlistId = existing.playlistId
                )
            } else {
                track
            }
            database.upsertTrack(merged)
        }

        return tracks
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

    private fun formatTrackTime(trackTimeMillis: Long?): String {
        val safeMillis = trackTimeMillis ?: return "00:00"
        val totalSeconds = safeMillis / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return "%02d:%02d".format(minutes, seconds)
    }

    private fun generateFallbackId(trackName: String?, artistName: String?): Long {
        return "${trackName.orEmpty()}-${artistName.orEmpty()}".hashCode().toLong()
    }
}
