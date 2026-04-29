package com.example.playlistmaker.domain.interactor

import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.repository.TracksRepository

class TracksInteractorImpl(
    private val tracksRepository: TracksRepository
) : TracksInteractor {
    override fun getTrackDetails(trackId: String): String {
        return tracksRepository.getTrackDetails(trackId)
    }

    override suspend fun getAllTracks(): List<Track> {
        return tracksRepository.getAllTracks()
    }
}
