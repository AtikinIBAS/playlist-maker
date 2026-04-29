package com.example.playlistmaker.data.repository

import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.db.toEntity
import com.example.playlistmaker.data.db.toTrack
import com.example.playlistmaker.data.network.ITunesApiService
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.repository.TracksRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.IOException

class TracksRepositoryImpl(
    private val iTunesApiService: ITunesApiService,
    appDatabase: AppDatabase,
    private val seedTracks: List<Track>,
) : TracksRepository {
    private val dao = appDatabase.tracksDao()

    override fun getTrackDetails(trackId: String): String {
        return seedTracks.find { it.id.toString() == trackId }?.trackName
            ?: "Track details for $trackId"
    }

    override suspend fun getAllTracks(): List<Track> {
        delay(300)
        ensureSeedTracks()
        return dao.getAllTracks().map { it.toTrack() }
    }

    override suspend fun searchTracks(expression: String): List<Track> {
        ensureSeedTracks()
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

        val mergedTracks = tracks.map { track ->
            val existing = dao.findTrackById(track.id)?.toTrack()
            val merged = if (existing != null) {
                track.copy(
                    favorite = existing.favorite,
                    playlistId = existing.playlistId
                )
            } else {
                track
            }
            dao.insertTrack(merged.toEntity())
            merged
        }

        return mergedTracks
    }

    override fun getTrackById(trackId: Long): Flow<Track?> {
        return dao.getTrackById(trackId).map { it?.toTrack() }
    }

    override fun getTrackByNameAndArtist(track: Track): Flow<Track?> {
        return dao.getTrackByNameAndArtist(track.trackName, track.artistName).map { it?.toTrack() }
    }

    override fun getFavoriteTracks(): Flow<List<Track>> {
        return dao.getFavoriteTracks().map { entities ->
            entities.map { it.toTrack() }
        }
    }

    override suspend fun insertTrackToPlaylist(track: Track, playlistId: Long) {
        val updatedTrack = track.copy(playlistId = playlistId)
        dao.insertTrack(updatedTrack.toEntity())
    }

    override suspend fun deleteTrackFromPlaylist(track: Track) {
        dao.insertTrack(track.copy(playlistId = 0).toEntity())
    }

    override suspend fun updateTrackFavoriteStatus(track: Track, isFavorite: Boolean) {
        val updatedTrack = track.copy(favorite = isFavorite)
        dao.insertTrack(updatedTrack.toEntity())
    }

    override suspend fun deleteTracksByPlaylistId(playlistId: Long) {
        dao.getTracksByPlaylistId(playlistId).forEach { entity ->
            dao.insertTrack(entity.copy(playlistId = 0))
        }
    }

    private suspend fun ensureSeedTracks() {
        val existingIds = dao.getAllTracks().map { it.id }.toSet()
        val missingTracks = seedTracks.filterNot { it.id in existingIds }
        if (missingTracks.isNotEmpty()) {
            dao.insertTracks(missingTracks.map { it.toEntity() })
        }
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
