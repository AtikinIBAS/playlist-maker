package com.example.playlistmaker.data.network

import com.example.playlistmaker.creator.Storage
import com.example.playlistmaker.data.dto.BaseResponse
import com.example.playlistmaker.data.dto.TrackDto
import com.example.playlistmaker.data.dto.TracksSearchRequest
import com.example.playlistmaker.data.dto.TracksSearchResponse
import com.example.playlistmaker.domain.network.NetworkClient

class RetrofitNetworkClient(
    private val storage: Storage
) : NetworkClient {

    override fun doRequest(dto: Any): BaseResponse {
        return when (dto) {
            is TracksSearchRequest -> {
                val searchList = storage.getAllTracks()
                    .filter {
                        it.trackName.contains(dto.expression, ignoreCase = true) ||
                            it.artistName.contains(dto.expression, ignoreCase = true)
                    }
                    .map { track ->
                        val parts = track.trackTime.split(":")
                        val minutes = parts.getOrNull(0)?.toIntOrNull() ?: 0
                        val seconds = parts.getOrNull(1)?.toIntOrNull() ?: 0
                        TrackDto(
                            trackName = track.trackName,
                            artistName = track.artistName,
                            trackTimeMillis = (minutes * 60 + seconds) * 1000
                        )
                    }
                TracksSearchResponse(searchList).apply { resultCode = 200 }
            }

            else -> BaseResponse().apply { resultCode = 400 }
        }
    }
}
