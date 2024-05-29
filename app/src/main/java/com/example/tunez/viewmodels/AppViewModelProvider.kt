package com.example.tunez.viewmodels

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.tunez.SpotifyPlaygroundApplication
import com.example.tunez.activities.BaseActivity
import com.example.tunez.activities.MainActivity
import com.example.tunez.ui.service.SpotifyService

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            HomeViewModel(
                spotifyService = application().spotifyService,
                application = application()
            )
        }
        initializer {
            SearchViewModel(
                spotifyService = application().spotifyService
            )
        }
        initializer {
            ReleasesViewModel(
                spotifyService = application().spotifyService
            )
        }
        initializer {
            RecommendationsViewModel(
                spotifyService = application().spotifyService
            )
        }
        initializer {
            ProfileViewModel()
        }
    }
}

fun CreationExtras.application(): SpotifyPlaygroundApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as SpotifyPlaygroundApplication)