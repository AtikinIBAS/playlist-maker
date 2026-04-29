package com.example.playlistmaker.data.network

class NetworkClientImpl : NetworkClient {
    override fun getTrackDetails(trackId: String): String {
        return "Track details for $trackId"
    }
}
