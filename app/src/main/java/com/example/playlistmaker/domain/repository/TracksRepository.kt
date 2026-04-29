package com.example.playlistmaker.domain.repository

import com.example.playlistmaker.domain.model.Track

interface TracksRepository {
    fun getTrackDetails(trackId: String): String
    suspend fun getAllTracks(): List<Track>
}
