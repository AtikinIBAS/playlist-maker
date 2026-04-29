package com.example.playlistmaker

import android.app.Application
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.room.Room
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.preferences.SearchHistoryPreferences
import com.example.playlistmaker.domain.interactor.TracksInteractor
import com.example.playlistmaker.domain.repository.PlaylistsRepository
import com.example.playlistmaker.domain.repository.TracksRepository
import java.io.File

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
    val searchHistoryPreferences: SearchHistoryPreferences by lazy {
        val dataStore = PreferenceDataStoreFactory.create(
            produceFile = { File(filesDir, "search_history.preferences_pb") }
        )
        SearchHistoryPreferences(dataStore)
    }

    override fun onCreate() {
        super.onCreate()
        Creator.init(database, searchHistoryPreferences)
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
