package com.example.playlistmaker

import android.app.Application
import androidx.room.Room
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.domain.interactor.TracksInteractor
import com.example.playlistmaker.domain.repository.PlaylistsRepository
import com.example.playlistmaker.domain.repository.TracksRepository

class PlaylistMakerApp : Application() {
    val database: AppDatabase by lazy {
        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "playlist-maker-room.db"
        )
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()
    }

    fun provideTracksInteractor(): TracksInteractor {
        return Creator.provideTracksInteractor()
    }

    fun provideTracksRepository(): TracksRepository {
        return Creator.getTracksRepository()
    }

    fun providePlaylistsRepository(): PlaylistsRepository {
        return Creator.getPlaylistsRepository()
    }
}
