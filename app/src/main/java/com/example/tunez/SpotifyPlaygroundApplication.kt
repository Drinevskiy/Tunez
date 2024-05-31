package com.example.tunez

import android.app.Application
import android.content.Context
import com.example.tunez.data.Model
import com.example.tunez.ui.service.SpotifyBroadcastReceiver
import com.example.tunez.ui.service.SpotifyService
import com.example.tunez.utils.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin

class SpotifyPlaygroundApplication : Application() {
    lateinit var model: Model
    lateinit var spotifyService: SpotifyService

    override fun onCreate() {
        super.onCreate()
        model = Model
        context = applicationContext
        spotifyService = SpotifyService()
        startKoin {
            androidLogger()
            androidContext(this@SpotifyPlaygroundApplication)
            modules(appModule)
        }
    }

    companion object {
        lateinit var context: Context
    }
}