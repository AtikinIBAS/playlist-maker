package com.example.playlistmaker.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlists")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "playlist_id")
    val id: Long = 0,
    @ColumnInfo(name = "playlist_name")
    val name: String,
    @ColumnInfo(name = "playlist_description")
    val description: String,
    @ColumnInfo(name = "playlist_image_path")
    val imagePath: String = "",
)
