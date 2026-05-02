package com.example.playlistmaker.creator

import com.example.playlistmaker.domain.model.Track

class Storage {
    private val listTracks = listOf(
        Track(id = 1, trackName = "Владивосток 2000", artistName = "Мумий Тролль", trackTime = "2:38"),
        Track(id = 2, trackName = "Группа крови", artistName = "Кино", trackTime = "4:43"),
        Track(id = 3, trackName = "Не смотри назад", artistName = "Ария", trackTime = "5:12"),
        Track(id = 4, trackName = "Звезда по имени Солнце", artistName = "Кино", trackTime = "3:45"),
        Track(id = 5, trackName = "Лондон", artistName = "Аквариум", trackTime = "4:32"),
        Track(id = 6, trackName = "На заре", artistName = "Альянс", trackTime = "3:50"),
        Track(id = 7, trackName = "Перемен", artistName = "Кино", trackTime = "4:56"),
        Track(id = 8, trackName = "Розовый фламинго", artistName = "Сплин", trackTime = "3:15"),
        Track(id = 9, trackName = "Танцевать", artistName = "Мельница", trackTime = "3:42"),
        Track(id = 10, trackName = "Чёрный бумер", artistName = "Серёга", trackTime = "4:01")
    )

    fun getAllTracks(): List<Track> = listTracks
}
