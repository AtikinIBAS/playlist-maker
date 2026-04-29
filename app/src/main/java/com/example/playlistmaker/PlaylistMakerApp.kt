package com.example.playlistmaker

import android.app.Application
import com.example.playlistmaker.data.network.NetworkClient
import com.example.playlistmaker.data.network.NetworkClientImpl
import com.example.playlistmaker.data.repository.TracksRepositoryImpl
import com.example.playlistmaker.domain.interactor.TracksInteractor
import com.example.playlistmaker.domain.interactor.TracksInteractorImpl
import com.example.playlistmaker.domain.repository.TracksRepository

class PlaylistMakerApp : Application() {

    private fun getNetworkClient(): NetworkClient {
        return NetworkClientImpl()
    }

    private fun getRepository(): TracksRepository {
        return TracksRepositoryImpl(getNetworkClient())
    }

    fun provideTracksInteractor(): TracksInteractor {
        return TracksInteractorImpl(getRepository())
    }
}
