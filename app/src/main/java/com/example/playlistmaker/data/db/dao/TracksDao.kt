package com.example.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.data.db.entity.TrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TracksDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: TrackEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTracks(tracks: List<TrackEntity>)

    @Query("SELECT * FROM tracks WHERE track_name = :name AND artist_name = :artist LIMIT 1")
    fun getTrackByNameAndArtist(name: String, artist: String): Flow<TrackEntity?>

    @Query("SELECT * FROM tracks WHERE track_id = :trackId LIMIT 1")
    fun getTrackById(trackId: Long): Flow<TrackEntity?>

    @Query("SELECT * FROM tracks WHERE track_id = :trackId LIMIT 1")
    suspend fun findTrackById(trackId: Long): TrackEntity?

    @Query("SELECT * FROM tracks ORDER BY track_id ASC")
    fun getAllTracksFlow(): Flow<List<TrackEntity>>

    @Query("SELECT * FROM tracks ORDER BY track_id ASC")
    suspend fun getAllTracks(): List<TrackEntity>

    @Query("SELECT COUNT(*) FROM tracks")
    suspend fun getTracksCount(): Int

    @Query("SELECT * FROM tracks WHERE favorite = 1")
    fun getFavoriteTracks(): Flow<List<TrackEntity>>

    @Query("SELECT * FROM tracks WHERE playlist_id = :playlistId")
    suspend fun getTracksByPlaylistId(playlistId: Long): List<TrackEntity>

    @Query("DELETE FROM tracks WHERE track_id = :trackId")
    suspend fun deleteTrackById(trackId: Long)
}
