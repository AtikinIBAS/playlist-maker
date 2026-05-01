package com.example.playlistmaker.data.network

import com.example.playlistmaker.creator.Storage
import com.example.playlistmaker.data.dto.BaseResponse
import com.example.playlistmaker.data.dto.TracksSearchRequest
import com.example.playlistmaker.data.dto.TracksSearchResponse
import com.example.playlistmaker.domain.network.NetworkClient

class RetrofitNetworkClient(
    private val iTunesApiService: ITunesApiService,
    private val storage: Storage
) : NetworkClient {

    override suspend fun doRequest(dto: Any): BaseResponse {
        return when (dto) {
            is TracksSearchRequest -> {
                try {
                    val response = iTunesApiService.searchTracks(expression = dto.expression)
                    if (response.isSuccessful) {
                        response.body()?.apply { resultCode = 200 } ?: BaseResponse().apply { resultCode = 500 }
                    } else {
                        BaseResponse().apply { resultCode = response.code() }
                    }
                } catch (_: Exception) {
                    BaseResponse().apply { resultCode = 500 }
                }
            }

            else -> BaseResponse().apply { resultCode = 400 }
        }
    }
}
