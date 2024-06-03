package com.example.tunez.roles

import com.example.tunez.content.Playlist

//interface IAccount {
//    var id: Int;
//    var email: String;
//    var genres: List<String>;
//    var playlists: List<Playlist>
//    var favoriteTracks: List<Track>
//    fun enter()
//    fun exit()
//    fun registration()
//    fun search(str: String): List<Track>
//    fun play(track: Track)
//    fun play(album: Album)
//    fun addToFavoriteTracks(track: Track)
//    fun removeFromFavoriteTracks(track: Track)
//    fun createPlaylist(str: String)
//    fun addToPlaylist(playlist: Playlist, track: Track)
//    fun removeFromPlaylist(playlist: Playlist, track: Track)
//}

open class IAccount(
    var uid: String? = null,
    var username: String? = null,
    var email: String? = null,
    var role: String? = null,
    var genres: List<String> = listOf(),
    val favouritePlaylist: Playlist = Playlist(),
    val playlists: List<Playlist> = listOf()
)