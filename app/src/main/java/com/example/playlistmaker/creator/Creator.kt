package com.example.playlistmaker.creator

import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.data.repository.TracksRepositoryImpl
import com.example.playlistmaker.domain.interactor.TracksInteractor
import com.example.playlistmaker.domain.interactor.TracksInteractorImpl
import com.example.playlistmaker.domain.network.NetworkClient
import com.example.playlistmaker.domain.repository.TracksRepository

object Creator {
    private val storage by lazy { Storage() }

    private fun getNetworkClient(): NetworkClient {
        return RetrofitNetworkClient(storage)
    }

    fun getTracksRepository(): TracksRepository {
        return TracksRepositoryImpl(getNetworkClient())
    }

    fun provideTracksInteractor(): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository())
    }
}
