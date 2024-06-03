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
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ReleasesViewModel(val spotifyService: SpotifyService): ViewModel() {
    private var _uiState = MutableStateFlow(ReleasesUiState())
    val releasesUiState: StateFlow<ReleasesUiState> = _uiState.asStateFlow()
    init {
        getReleases()
        getCharts()
    }


    fun updateUiState(uiState: ReleasesUiState){
        _uiState.update {
            it.copy(
                releases = uiState.releases,
                charts = uiState.charts,
                chartsString = uiState.chartsString
            )
        }
    }
    private fun getCharts() {
        val chart = Firebase.database.reference.child("Chart")
        chart.get().addOnCompleteListener {
            val dates = it.result.value as? Map<String, List<Map<String, Long>>> ?: emptyMap()
            val lastSevenDays = getLastSevenDays()
            val tracks: MutableMap<String, Long> = mutableMapOf()
            dates.forEach { (date, tracksData) ->
                Log.i("firebase", "ForEach $date $lastSevenDays")
                if (date in lastSevenDays) {
                    tracksData.forEach { trackData ->
                        trackData.forEach { (trackName, count) ->
                            tracks[trackName] = (tracks[trackName] ?: 0) + count
                        }
                    }
                }
            }
            val result = tracks.toList()
                .sortedByDescending { it.second }
                .toMap()
            viewModelScope.launch {
                val chartsString: List<String> = result.keys.toList()
                val charts = spotifyService.stringUrisToTracks(chartsString)
                updateUiState(releasesUiState.value.copy(charts = charts, chartsString = chartsString))
            }
        }
    }

    private fun getLastSevenDays(): List<String> {
        val today = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return (0..6).map { daysAgo ->
            today.minusDays(daysAgo.toLong()).format(formatter)
        }
    }
    fun getReleases() {
        viewModelScope.launch {
            val releases = spotifyService.getNewReleases()!!
            updateUiState(releasesUiState.value.copy(releases = releases))
        }
    }

    fun play(uri: PlayableUri){
        viewModelScope.launch {
            spotifyService.play(uri)
        }
    }


}

data class ReleasesUiState(
    var releases: List<Track> = listOf(),
    var charts: List<Track?> = listOf(),
    var chartsString: List<String> = listOf(),
)