package com.example.tunez.content


data class Playlist (
    var durationInMs: Int = 0,
    var name: String = "No name",
    var tracks: List<String> = listOf(),
    var image: String? = null,
    var id: String? = null
)