package com.example.tunez.content

import java.time.Duration

//import com.example.tunez.roles.Artist

data class Track(
    var id: String? = null,
    var name: String? = null,
//    var durationInMs: Long? = null,
    var blocked: Boolean = false,
    var edited: Boolean = false,
    var reason: String? = null,
    var count: Long = 0,
    var artistId: String? = null,
)