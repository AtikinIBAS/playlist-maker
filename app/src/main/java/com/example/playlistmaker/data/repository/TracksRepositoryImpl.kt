package com.example.playlistmaker.data.repository

import com.example.playlistmaker.data.network.NetworkClient
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.repository.TracksRepository
import kotlinx.coroutines.delay

class TracksRepositoryImpl(
    private val networkClient: NetworkClient
) : TracksRepository {
    private val listTracks = listOf(
        Track(
            trackName = "Владивосток 2000",
            artistName = "Мумий Тролль",
            trackTime = "2:38"
        ),
        Track(
            trackName = "Группа крови",
            artistName = "Кино",
            trackTime = "4:43"
        ),
        Track(
            trackName = "Не смотри назад",
            artistName = "Ария",
            trackTime = "5:12"
        ),
        Track(
            trackName = "Звезда по имени Солнце",
            artistName = "Кино",
            trackTime = "3:45"
        ),
        Track(
            trackName = "Лондон",
            artistName = "Аквариум",
            trackTime = "4:32"
        ),
        Track(
            trackName = "На заре",
            artistName = "Альянс",
            trackTime = "3:50"
        ),
        Track(
            trackName = "Перемен",
            artistName = "Кино",
            trackTime = "4:56"
        ),
        Track(
            trackName = "Розовый фламинго",
            artistName = "Сплин",
            trackTime = "3:15"
        ),
        Track(
            trackName = "Танцевать",
            artistName = "Мельница",
            trackTime = "3:42"
        ),
        Track(
            trackName = "Чёрный бумер",
            artistName = "Серёга",
            trackTime = "4:01"
        )
    )

    override fun getTrackDetails(trackId: String): String {
        return networkClient.getTrackDetails(trackId)
    }

    override suspend fun getAllTracks(): List<Track> {
        delay(1000)
        return listTracks
    }
}
