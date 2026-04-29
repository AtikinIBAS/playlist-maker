package com.example.playlistmaker.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.playlistmaker.data.db.dao.RoomTrackDao
import com.example.playlistmaker.data.db.entity.RoomTrackEntity

@Database(
    version = 1,
    exportSchema = true,
    entities = [RoomTrackEntity::class]
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun roomTrackDao(): RoomTrackDao
}
