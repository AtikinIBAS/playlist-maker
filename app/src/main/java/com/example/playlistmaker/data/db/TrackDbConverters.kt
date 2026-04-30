package com.example.playlistmaker.data.db

import com.example.playlistmaker.data.db.entity.PlaylistEntity
import com.example.playlistmaker.data.db.entity.TrackEntity
import com.example.playlistmaker.domain.model.Playlist
import com.example.playlistmaker.domain.model.Track

fun TrackEntity.toTrack(): Track {
    return Track(
        id = id,
        trackName = trackName,
        artistName = artistName,
        trackTime = trackTime,
        image = image,
        favorite = favorite,
        playlistId = playlistId
    )
}

fun Track.toEntity(): TrackEntity {
    return TrackEntity(
        id = id,
        trackName = trackName,
        artistName = artistName,
        trackTime = trackTime,
        image = image,
        favorite = favorite,
        playlistId = playlistId
    )
}

fun PlaylistEntity.toPlaylist(tracks: List<Track> = emptyList()): Playlist {
    return Playlist(
        id = id,
        name = name,
        description = description,
        imagePath = imagePath,
        tracks = tracks
    )
}
