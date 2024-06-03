package com.example.tunez.activities

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import com.adamratzman.spotify.auth.implicit.startSpotifyImplicitLoginActivity
import com.example.tunez.SpotifyPlaygroundApplication
import com.example.tunez.auth.SpotifyImplicitLoginActivityImpl

class StartActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        myApplication.spotifyService.getDevices()
        applicationContext.packageManager.getLaunchIntentForPackage("com.spotify.music")?.let { applicationContext.startActivity(it) }
//        startSpotifyImplicitLoginActivity(SpotifyImplicitLoginActivityImpl::class.java)
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}