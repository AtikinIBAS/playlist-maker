package com.example.playlistmaker.data.dto

class TracksSearchResponse(
    val resultCount: Int? = null,
    val results: List<TrackDto> = emptyList()
) : BaseResponse()
