package com.example.tunez.auth

import android.content.Intent
import com.adamratzman.spotify.SpotifyImplicitGrantApi
import com.adamratzman.spotify.SpotifyScope
import com.adamratzman.spotify.auth.implicit.AbstractSpotifyAppImplicitLoginActivity
import com.example.tunez.BuildConfig
import com.example.tunez.SpotifyPlaygroundApplication
import com.example.tunez.activities.MainActivity
import com.example.tunez.utils.toast

class SpotifyImplicitLoginActivityImpl : AbstractSpotifyAppImplicitLoginActivity() {
    override val state: Int = 1337
    override val clientId: String = BuildConfig.SPOTIFY_CLIENT_ID
    override val redirectUri: String = BuildConfig.SPOTIFY_REDIRECT_URI_AUTH
    override val useDefaultRedirectHandler: Boolean = false
    override fun getRequestingScopes(): List<SpotifyScope> = SpotifyScope.entries

    override fun onSuccess(spotifyApi: SpotifyImplicitGrantApi) {
        val model = (application as SpotifyPlaygroundApplication).model
        model.credentialStore.setSpotifyApi(spotifyApi)
        toast("Authentication via spotify-auth has completed. Launching MainActivity..")
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    override fun onFailure(errorMessage: String) {
        toast("Auth failed: $errorMessage")
    }
}