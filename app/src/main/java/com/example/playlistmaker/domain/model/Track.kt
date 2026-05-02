package com.example.playlistmaker.domain.model

data class Track(
    val id: Long = 0,
    val trackName: String,
    val artistName: String,
    val trackTime: String,
    val image: String = "",
    var favorite: Boolean = false,
    var playlistId: Long = 0
)
