package com.example.playlistmaker.domain.model

data class Playlist(
    val id: Long = 0,
    val name: String,
    val description: String,
    val imagePath: String = "",
    var tracks: List<Track>
)
