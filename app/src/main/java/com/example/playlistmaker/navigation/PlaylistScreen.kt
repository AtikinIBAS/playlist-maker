package com.example.playlistmaker.navigation

enum class PlaylistScreen(val route: String) {
    MAIN("main"),
    SONGS("songs"),
    PLAYLISTS("playlists"),
    PLAYLIST_DETAILS("playlist_details"),
    FAVORITES("favorites"),
    NEW_PLAYLIST("new_playlist"),
    TRACK_DETAILS("track_details"),
    SETTINGS("settings");

    fun routeWithArg(arg: Long): String = "$route/$arg"
}
