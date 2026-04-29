package com.example.playlistmaker.domain.interactor

import com.example.playlistmaker.domain.model.Track

interface TracksInteractor {
    fun getTrackDetails(trackId: String): String
    suspend fun getAllTracks(): List<Track>
}
