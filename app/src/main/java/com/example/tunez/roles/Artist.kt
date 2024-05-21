package com.example.tunez.roles

import com.example.tunez.content.Album
import com.example.tunez.content.Playlist
import com.example.tunez.content.Track

class Artist : IAccount {
    override var id: Int
        get() = TODO("Not yet implemented")
        set(value) {}
    override var email: String
        get() = TODO("Not yet implemented")
        set(value) {}
    override var password: String
        get() = TODO("Not yet implemented")
        set(value) {}
    override var genres: List<String>
        get() = TODO("Not yet implemented")
        set(value) {}
    override var artists: List<Artist>
        get() = TODO("Not yet implemented")
        set(value) {}
    override var playlists: List<Playlist>
        get() = TODO("Not yet implemented")
        set(value) {}
    override var favoriteTracks: List<Track>
        get() = TODO("Not yet implemented")
        set(value) {}
    lateinit var tracks: List<Track>
    lateinit var albums: List<Album>

    override fun enter() {
        TODO("Not yet implemented")
    }

    override fun exit() {
        TODO("Not yet implemented")
    }

    override fun registration() {
        TODO("Not yet implemented")
    }

    override fun search(str: String): List<Track> {
        TODO("Not yet implemented")
    }

    override fun play(track: Track) {
        TODO("Not yet implemented")
    }

    override fun play(album: Album) {
        TODO("Not yet implemented")
    }

    override fun addToFavoriteTracks(track: Track) {
        TODO("Not yet implemented")
    }

    override fun removeFromFavoriteTracks(track: Track) {
        TODO("Not yet implemented")
    }

    override fun createPlaylist(str: String) {
        TODO("Not yet implemented")
    }

    override fun addToPlaylist(playlist: Playlist, track: Track) {
        TODO("Not yet implemented")
    }

    override fun removeFromPlaylist(playlist: Playlist, track: Track) {
        TODO("Not yet implemented")
    }

    fun addTrack(track: Track){
        TODO("Not yet implemented")
    }

    fun removeTrack(track: Track){
        TODO("Not yet implemented")
    }

    fun addAlbum(album: Album){
        TODO("Not yet implemented")
    }

    fun removeAlbum(album: Album){
        TODO("Not yet implemented")
    }

    fun getStatistics(): String{
        TODO("Not yet implemented")
    }
}