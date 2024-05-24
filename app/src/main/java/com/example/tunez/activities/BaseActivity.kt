package com.example.tunez.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import com.example.tunez.SpotifyPlaygroundApplication
import com.example.tunez.data.Model
import com.example.tunez.ui.service.SpotifyService

abstract class BaseActivity : AppCompatActivity() {
    lateinit var model: Model

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        model = (application as SpotifyPlaygroundApplication).model
    }
}