package com.example.playlistmaker.data.repository

import com.example.playlistmaker.data.dto.TracksSearchRequest
import com.example.playlistmaker.data.dto.TracksSearchResponse
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.network.NetworkClient
import com.example.playlistmaker.domain.repository.TracksRepository
import kotlinx.coroutines.delay

class TracksRepositoryImpl(
    private val networkClient: NetworkClient
) : TracksRepository {
    override fun getTrackDetails(trackId: String): String {
        return "Track details for $trackId"
    }

    override suspend fun getAllTracks(): List<Track> {
        return searchTracks("")
    }

    override suspend fun searchTracks(expression: String): List<Track> {
        val response = networkClient.doRequest(TracksSearchRequest(expression))
        delay(1000)

        return if (response.resultCode == 200) {
            (response as TracksSearchResponse).results.map {
                val seconds = it.trackTimeMillis / 1000
                val minutes = seconds / 60
                val trackTime = "%02d:%02d".format(minutes, seconds - minutes * 60)
                Track(
                    trackName = it.trackName,
                    artistName = it.artistName,
                    trackTime = trackTime
                )
            }
        } else {
            emptyList()
        }
    }
}
