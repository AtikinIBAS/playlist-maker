package com.example.playlistmaker.data.repository

import com.example.playlistmaker.data.network.NetworkClient
import com.example.playlistmaker.domain.repository.TracksRepository

class TracksRepositoryImpl(
    private val networkClient: NetworkClient
) : TracksRepository {
    override fun getTrackDetails(trackId: String): String {
        return networkClient.getTrackDetails(trackId)
    }
}
