package com.example.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.playlistmaker.data.db.entity.RoomTrackEntity

@Dao
interface RoomTrackDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: RoomTrackEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTracks(tracks: List<RoomTrackEntity>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateTrack(track: RoomTrackEntity)

    @Delete
    suspend fun deleteTrack(track: RoomTrackEntity)

    @Query("SELECT * FROM room_track_table")
    suspend fun getTracks(): List<RoomTrackEntity>

    @Query("SELECT * FROM room_track_table WHERE track_id = :trackId")
    suspend fun getTrackById(trackId: Long): RoomTrackEntity?
}
