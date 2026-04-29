package com.example.playlistmaker.creator

import com.example.playlistmaker.data.network.ITunesApiService
import com.example.playlistmaker.data.repository.PlaylistsRepositoryImpl
import com.example.playlistmaker.data.repository.SearchHistoryRepositoryImpl
import com.example.playlistmaker.data.repository.TracksRepositoryImpl
import com.example.playlistmaker.data.storage.DatabaseMock
import com.example.playlistmaker.domain.interactor.TracksInteractor
import com.example.playlistmaker.domain.interactor.TracksInteractorImpl
import com.example.playlistmaker.domain.repository.PlaylistsRepository
import com.example.playlistmaker.domain.repository.SearchHistoryRepository
import com.example.playlistmaker.domain.repository.TracksRepository
import kotlinx.coroutines.CoroutineScope
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Creator {
    private const val ITUNES_BASE_URL = "https://itunes.apple.com/"
    private val storage by lazy { Storage() }
    private val database by lazy { DatabaseMock(storage.getAllTracks()) }
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(ITUNES_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    private val iTunesApiService by lazy { retrofit.create(ITunesApiService::class.java) }
    private val tracksRepository by lazy { TracksRepositoryImpl(database, iTunesApiService) }
    private val playlistsRepository by lazy { PlaylistsRepositoryImpl(database) }

    fun getTracksRepository(): TracksRepository = tracksRepository

    fun getPlaylistsRepository(): PlaylistsRepository = playlistsRepository

    fun getSearchHistoryRepository(scope: CoroutineScope): SearchHistoryRepository {
        return SearchHistoryRepositoryImpl(database = database, scope = scope)
    }

    fun provideTracksInteractor(): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository())
    }
}
