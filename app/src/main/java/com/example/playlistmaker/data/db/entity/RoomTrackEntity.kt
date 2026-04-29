package com.example.playlistmaker.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "room_track_table")
data class RoomTrackEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "track_id")
    val id: Long = 0,
    @ColumnInfo(name = "track_title")
    val title: String,
    @ColumnInfo(name = "track_artist")
    val artist: String
)
