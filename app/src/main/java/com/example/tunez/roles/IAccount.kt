package com.example.tunez.roles

import com.example.tunez.content.Album
import com.example.tunez.content.Playlist
import com.example.tunez.content.Track

interface IAccount {
    var id: Int;
    var email: String;
    var password: String;
    var genres: List<String>;
    var artists: List<Artist>;
    var playlists: List<Playlist>
    var favoriteTracks: List<Track>
    fun enter()
    fun exit()
    fun registration()
    fun search(str: String): List<Track>
    fun play(track: Track)
    fun play(album: Album)
    fun addToFavoriteTracks(track: Track)
    fun removeFromFavoriteTracks(track: Track)
    fun createPlaylist(str: String)
    fun addToPlaylist(playlist: Playlist, track: Track)
    fun removeFromPlaylist(playlist: Playlist, track: Track)
}