package com.example.tunez.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adamratzman.spotify.models.PlayableUri
import com.adamratzman.spotify.models.Track
import com.example.tunez.activities.user
import com.example.tunez.data.Constants
import com.example.tunez.data.Constants.GENRES
import com.example.tunez.ui.service.SpotifyService
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.database
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.NullPointerException

class RecommendationsViewModel(val spotifyService: SpotifyService): ViewModel() {
    private var _uiState = MutableStateFlow(RecommendationUiState())
    val recommendationsUiState: StateFlow<RecommendationUiState> = _uiState.asStateFlow()
    init {
        getRecommendations()
    }
    fun updateUiState(uiState: RecommendationUiState){
        _uiState.update {
            it.copy(
                recommendations = uiState.recommendations,
            )
        }
    }

    fun getRecommendations() {
        viewModelScope.launch {
            val genres = listOf<String>()
            if(user != null) {
                val genres = getGenresFromFirebase()
            }
            val recs = spotifyService.getRecommendedTracks(genres)
            updateUiState(recommendationsUiState.value.copy(recommendations = recs))
        }
    }

    private suspend fun getGenresFromFirebase(): List<String> {
        return withContext(Dispatchers.IO) {
            val snapshot = Firebase.database.reference.child("Users").child(user!!.uid).child("genres").get().await()
            snapshot.value as List<String>
        }
    }
    fun play(uri: PlayableUri){
        viewModelScope.launch {
            spotifyService.play(uri)
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
}

data class RecommendationUiState(
    var recommendations: List<com.adamratzman.spotify.models.Track>? = listOf(),
)