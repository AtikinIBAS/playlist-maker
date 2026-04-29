package com.example.playlistmaker.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.playlistmaker.data.db.dao.TracksDao
import com.example.playlistmaker.data.db.entity.TrackEntity

@Database(
    version = 2,
    exportSchema = true,
    entities = [TrackEntity::class]
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun tracksDao(): TracksDao
}
