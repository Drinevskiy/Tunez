package com.example.tunez

import android.app.Application
import android.content.Context
import com.example.tunez.data.Model

class SpotifyPlaygroundApplication : Application() {
    lateinit var model: Model

    override fun onCreate() {
        super.onCreate()
        model = Model
        context = applicationContext
    }

    companion object {
        lateinit var context: Context
    }
}