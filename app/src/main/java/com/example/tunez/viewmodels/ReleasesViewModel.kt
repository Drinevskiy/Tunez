package com.example.tunez.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adamratzman.spotify.models.PlayableUri
import com.adamratzman.spotify.models.Track
import com.example.tunez.activities.user
import com.example.tunez.ui.service.SpotifyService
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.database
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.lang.NullPointerException

class ReleasesViewModel(val spotifyService: SpotifyService): ViewModel() {
    private var _uiState = MutableStateFlow(ReleasesUiState())
    val releasesUiState: StateFlow<ReleasesUiState> = _uiState.asStateFlow()
    init {
        getReleases()
    }
    fun updateUiState(uiState: ReleasesUiState){
        _uiState.update {
            it.copy(
                releases = uiState.releases,
            )
        }
    }

    fun getReleases() {
        viewModelScope.launch {
            val releases = spotifyService.getNewReleases()
            updateUiState(releasesUiState.value.copy(releases = releases))
        }
    }

    fun addToFavouriteTracks(track: Track){
        var favTracks = listOf<String>()
        var duration = 0
        if(user != null) {
            viewModelScope.launch {
                try {
                    val db = Firebase.database.reference
                    val snapshot = db.child("Users")
                        .child(Firebase.auth.currentUser!!.uid)
                        .child("favouritePlaylist")
                        .child("favouriteTracks").get().await()
                    favTracks = snapshot.value as? List<String> ?: emptyList()
                    if (track.uri.uri !in favTracks) {
                        favTracks = favTracks.plus(track.uri.uri)
                    }
                    val tracks = favTracks.map { spotifyService.stringUriToTrack(it) }
                    duration = tracks.sumOf { it?.length ?: 0 }


                    db.child("Users")
                        .child(user!!.uid)
                        .child("favouritePlaylist")
                        .setValue(
                            mapOf(
                                "favouriteTracks" to favTracks,
                                "name" to "Favourite Tracks",
                                "duration" to duration
                            )
                        )
                } catch (ex: Exception) {
                    Log.e("firebase", "Error getting/setting data", ex)
                }
            }
        }
    }
    fun play(uri: PlayableUri){
        viewModelScope.launch {
            spotifyService.play(uri)
        }
    }


}

data class ReleasesUiState(
    var releases: List<com.adamratzman.spotify.models.Track>? = listOf(),
)