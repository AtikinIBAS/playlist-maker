package com.example.playlistmaker.creator

import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.network.ITunesApiService
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.data.preferences.SearchHistoryPreferences
import com.example.playlistmaker.data.repository.PlaylistsRepositoryImpl
import com.example.playlistmaker.data.repository.SearchHistoryRepositoryImpl
import com.example.playlistmaker.data.repository.TracksRepositoryImpl
import com.example.playlistmaker.domain.interactor.TracksInteractor
import com.example.playlistmaker.domain.interactor.TracksInteractorImpl
import com.example.playlistmaker.domain.repository.PlaylistsRepository
import com.example.playlistmaker.domain.repository.SearchHistoryRepository
import com.example.playlistmaker.domain.repository.TracksRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Creator {
    private const val ITUNES_BASE_URL = "https://itunes.apple.com/"
    private val storage by lazy { Storage() }
    private lateinit var appDatabase: AppDatabase
    private lateinit var searchHistoryPreferences: SearchHistoryPreferences
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(ITUNES_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    private val iTunesApiService by lazy { retrofit.create(ITunesApiService::class.java) }
    private val networkClient by lazy { RetrofitNetworkClient(iTunesApiService, storage) }
    private val tracksRepository by lazy {
        TracksRepositoryImpl(
            networkClient = networkClient,
            appDatabase = appDatabase,
            seedTracks = storage.getAllTracks()
        )
    }
    private val playlistsRepository by lazy { PlaylistsRepositoryImpl(appDatabase) }

    fun init(database: AppDatabase, preferences: SearchHistoryPreferences) {
        if (!::appDatabase.isInitialized) {
            appDatabase = database
        }
        if (!::searchHistoryPreferences.isInitialized) {
            searchHistoryPreferences = preferences
        }
    }

    fun getTracksRepository(): TracksRepository = tracksRepository

    fun getPlaylistsRepository(): PlaylistsRepository = playlistsRepository

    fun getSearchHistoryRepository(): SearchHistoryRepository {
        return SearchHistoryRepositoryImpl(searchHistoryPreferences)
    }

    fun provideTracksInteractor(): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository())
    }
}
