package com.example.tunez.roles

import com.example.tunez.content.Playlist

class User(
    uid: String? = null,
    username: String? = null,
    email: String? = null,
    genres: List<String> = listOf(),
    favouritePlaylist: Playlist = Playlist(),
    playlists: List<Playlist> = listOf()
) : IAccount(
    uid, username, email, "user" ,genres, favouritePlaylist, playlists
)