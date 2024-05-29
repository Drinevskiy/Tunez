package com.example.tunez.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adamratzman.spotify.models.PlayableUri
import com.example.tunez.ui.service.SpotifyService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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

    fun play(uri: PlayableUri){
        viewModelScope.launch {
            spotifyService.play(uri)
        }
    }


}

data class ReleasesUiState(
    var releases: List<com.adamratzman.spotify.models.Track>? = listOf(),
)