package com.example.tunez

import android.app.Application
import android.content.Context
import com.example.tunez.data.Model
import com.example.tunez.ui.service.SpotifyBroadcastReceiver
import com.example.tunez.ui.service.SpotifyService

class SpotifyPlaygroundApplication : Application() {
    lateinit var model: Model
    lateinit var spotifyService: SpotifyService

    override fun onCreate() {
        super.onCreate()
        model = Model
        context = applicationContext
        spotifyService = SpotifyService()
    }

    companion object {
        lateinit var context: Context
    }
}