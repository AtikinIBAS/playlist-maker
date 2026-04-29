package com.example.playlistmaker.data.network

interface NetworkClient {
    fun getTrackDetails(trackId: String): String
}
