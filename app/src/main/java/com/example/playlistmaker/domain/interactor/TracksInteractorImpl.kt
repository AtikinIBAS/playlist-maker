package com.example.playlistmaker.domain.interactor

import com.example.playlistmaker.domain.repository.TracksRepository

class TracksInteractorImpl(
    private val tracksRepository: TracksRepository
) : TracksInteractor {
    override fun getTrackDetails(trackId: String): String {
        return tracksRepository.getTrackDetails(trackId)
    }
}
