package com.example.tunez.content

import com.adamratzman.spotify.models.Track


data class Playlist (
    var durationInMs: Int = 0,
    var name: String = "No name",
    var tracks: List<Track?> = listOf(),
    var image: String? = null,
    var id: String? = null
)