package com.example.playlistmaker.ui.track

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.PlaylistMakerApp
import com.example.playlistmaker.domain.interactor.TracksInteractor

class TrackViewModel(
    private val trackId: String,
    private val tracksInteractor: TracksInteractor
) : ViewModel() {

    init {
        Log.d("TEST", "init!: $trackId")
    }

    fun getTrackPreview(): String {
        return tracksInteractor.getTrackDetails(trackId)
    }

    companion object {
        fun getViewModelFactory(trackId: String): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val interactor = (this[APPLICATION_KEY] as PlaylistMakerApp).provideTracksInteractor()

                TrackViewModel(
                    trackId = trackId,
                    tracksInteractor = interactor
                )
            }
        }
    }
}
