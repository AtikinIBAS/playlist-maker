package com.example.playlistmaker.data.repository

import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.db.toPlaylist
import com.example.playlistmaker.data.db.toTrack
import com.example.playlistmaker.data.db.entity.PlaylistEntity
import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.domain.repository.PlaylistsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class PlaylistsRepositoryImpl(
    database: AppDatabase
) : PlaylistsRepository {
    private val playlistDao = database.playlistDao()
    private val tracksDao = database.tracksDao()

    override fun getPlaylist(playlistId: Long): Flow<Playlist?> {
        return combine(
            playlistDao.getPlaylistById(playlistId),
            tracksDao.getAllTracksFlow()
        ) { playlistEntity, tracks ->
            playlistEntity?.toPlaylist(
                tracks = tracks
                    .filter { it.playlistId == playlistId }
                    .map { it.toTrack() }
            )
        }
    }

    override fun getAllPlaylists(): Flow<List<Playlist>> {
        return combine(
            playlistDao.getAllPlaylists(),
            tracksDao.getAllTracksFlow()
        ) { playlistEntities, tracks ->
            playlistEntities.map { playlistEntity ->
                playlistEntity.toPlaylist(
                    tracks = tracks
                        .filter { it.playlistId == playlistEntity.id }
                        .map { it.toTrack() }
                )
            }
        }
    }

    override suspend fun addNewPlaylist(name: String, description: String) {
        playlistDao.insertPlaylist(
            PlaylistEntity(
                name = name,
                description = description
            )
        )
    }

    override suspend fun deletePlaylistById(id: Long) {
        playlistDao.deletePlaylistById(id)
    }
}
