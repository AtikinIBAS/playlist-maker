package com.example.playlistmaker.data.repository

import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.db.toEntity
import com.example.playlistmaker.data.db.toFavoriteEntity
import com.example.playlistmaker.data.db.toTrack
import com.example.playlistmaker.data.dto.TracksSearchRequest
import com.example.playlistmaker.data.dto.TracksSearchResponse
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.network.NetworkClient
import com.example.playlistmaker.domain.repository.TracksRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.io.IOException

class TracksRepositoryImpl(
    private val networkClient: NetworkClient,
    appDatabase: AppDatabase,
    private val seedTracks: List<Track>,
) : TracksRepository {
    private val dao = appDatabase.tracksDao()
    private val favoriteTracksDao = appDatabase.favoriteTracksDao()

    override fun getTrackDetails(trackId: String): String {
        return seedTracks.find { it.id.toString() == trackId }?.trackName
            ?: "Track details for $trackId"
    }

    override suspend fun getAllTracks(): List<Track> {
        return dao.getAllTracks().map { it.toTrack() }
    }

    override suspend fun searchTracks(expression: String): List<Track> {
        val response = networkClient.doRequest(TracksSearchRequest(expression))
        if (response.resultCode != 200 || response !is TracksSearchResponse) {
            throw IOException(response.resultCode.toString())
        }

        val tracks = response.results
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
            val playlistTrack = dao.findTrackById(track.id)?.toTrack()
            val favoriteTrack = favoriteTracksDao.findTrackById(track.id)?.toTrack()
            val merged = if (playlistTrack != null || favoriteTrack != null) {
                track.copy(
                    favorite = favoriteTrack != null,
                    playlistId = playlistTrack?.playlistId ?: 0
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
        return combine(
            dao.getTrackById(trackId),
            favoriteTracksDao.getTrackById(trackId)
        ) { trackEntity, favoriteEntity ->
            val baseTrack = trackEntity?.toTrack() ?: favoriteEntity?.toTrack()
            baseTrack?.copy(
                favorite = favoriteEntity != null,
                playlistId = trackEntity?.playlistId ?: 0
            )
        }
    }

    override fun getTrackByNameAndArtist(track: Track): Flow<Track?> {
        return combine(
            dao.getTrackByNameAndArtist(track.trackName, track.artistName),
            favoriteTracksDao.getFavoriteTracks()
        ) { trackEntity, favorites ->
            val favoriteEntity = favorites.firstOrNull { it.id == track.id }
            val baseTrack = trackEntity?.toTrack() ?: favoriteEntity?.toTrack()
            baseTrack?.copy(
                favorite = favoriteEntity != null,
                playlistId = trackEntity?.playlistId ?: 0
            )
        }
    }

    override fun getFavoriteTracks(): Flow<List<Track>> {
        return favoriteTracksDao.getFavoriteTracks().map { entities ->
            entities.map { it.toTrack() }
        }
    }

    override suspend fun insertTrackToPlaylist(track: Track, playlistId: Long) {
        val updatedTrack = track.copy(playlistId = playlistId)
        dao.insertTrack(updatedTrack.toEntity())
    }

    override suspend fun deleteTrackFromPlaylist(track: Track) {
        dao.deleteTrackById(track.id)
    }

    override suspend fun updateTrackFavoriteStatus(track: Track, isFavorite: Boolean) {
        if (isFavorite) {
            favoriteTracksDao.insertTrack(track.copy(favorite = true).toFavoriteEntity())
        } else {
            favoriteTracksDao.deleteTrackById(track.id)
        }

        dao.findTrackById(track.id)?.let { existingTrack ->
            dao.insertTrack(existingTrack.copy(favorite = isFavorite))
        }
    }

    override suspend fun deleteTracksByPlaylistId(playlistId: Long) {
        dao.getTracksByPlaylistId(playlistId).forEach { entity -> dao.deleteTrackById(entity.id) }
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
