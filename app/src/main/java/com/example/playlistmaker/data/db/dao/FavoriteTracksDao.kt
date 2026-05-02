package com.example.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.data.db.entity.FavoriteTrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteTracksDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: FavoriteTrackEntity)

    @Query("SELECT * FROM favorite_tracks WHERE track_id = :trackId LIMIT 1")
    fun getTrackById(trackId: Long): Flow<FavoriteTrackEntity?>

    @Query("SELECT * FROM favorite_tracks WHERE track_id = :trackId LIMIT 1")
    suspend fun findTrackById(trackId: Long): FavoriteTrackEntity?

    @Query("SELECT * FROM favorite_tracks")
    fun getFavoriteTracks(): Flow<List<FavoriteTrackEntity>>

    @Query("DELETE FROM favorite_tracks WHERE track_id = :trackId")
    suspend fun deleteTrackById(trackId: Long)
}
