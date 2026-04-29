package com.example.playlistmaker

import android.app.Application
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.domain.interactor.TracksInteractor
import com.example.playlistmaker.domain.repository.TracksRepository

class PlaylistMakerApp : Application() {

    fun provideTracksInteractor(): TracksInteractor {
        return Creator.provideTracksInteractor()
    }

    fun provideTracksRepository(): TracksRepository {
        return Creator.getTracksRepository()
    }
}
