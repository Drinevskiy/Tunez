package com.example.tunez.data

import com.adamratzman.spotify.auth.SpotifyDefaultCredentialStore
import com.example.tunez.BuildConfig
import com.example.tunez.SpotifyPlaygroundApplication

object Model {
    val credentialStore by lazy {
        SpotifyDefaultCredentialStore(
            clientId = BuildConfig.SPOTIFY_CLIENT_ID,
            redirectUri = BuildConfig.SPOTIFY_REDIRECT_URI_PKCE,
            applicationContext = SpotifyPlaygroundApplication.context
        )
    }
}