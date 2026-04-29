package com.example.playlistmaker.domain.repository

interface TracksRepository {
    fun getTrackDetails(trackId: String): String
}
