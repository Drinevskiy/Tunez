package com.example.tunez.viewmodels

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adamratzman.spotify.models.SpotifyImage
import com.adamratzman.spotify.models.SpotifyTrackUriSerializer
import com.adamratzman.spotify.models.Track
import com.adamratzman.spotify.models.toTrackUri
import com.adamratzman.spotify.notifications.SpotifyBroadcastEventData
import com.adamratzman.spotify.notifications.SpotifyBroadcastType
import com.adamratzman.spotify.notifications.SpotifyMetadataChangedData
import com.adamratzman.spotify.notifications.SpotifyPlaybackStateChangedData
import com.adamratzman.spotify.notifications.registerSpotifyBroadcastReceiver
import com.example.tunez.SpotifyPlaygroundApplication
import com.example.tunez.ui.service.SpotifyBroadcastReceiver
import com.example.tunez.ui.service.SpotifyService
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(val spotifyService: SpotifyService, private val application: SpotifyPlaygroundApplication): AndroidViewModel(application) {
    private val context: Context
        get() = getApplication<SpotifyPlaygroundApplication>().applicationContext
    private val receiver = SpotifyBroadcastReceiver(this)
    private var _uiState = MutableStateFlow(HomeUiState())
    val homeUiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    fun updateUiState(uiState: HomeUiState){
        _uiState.update {
            it.copy(
                image = uiState.image,
                name = uiState.name,
                authors = uiState.authors,
                isPlaying = uiState.isPlaying,
                trackLength = uiState.trackLength,
                position = uiState.position
            )
        }
    }

    init{
        context.registerSpotifyBroadcastReceiver(receiver, *SpotifyBroadcastType.entries.toTypedArray())
    }

    fun handleMetadata(data: SpotifyMetadataChangedData){
        viewModelScope.launch {
            val track = spotifyService.playableUriToTrack(data.playableUri)
            updateUiState(homeUiState.value.copy(
                image = track?.album?.images?.get(0),
                name = data.trackName,
                authors = listOf(data.artistName),
                trackLength = data.trackLengthInSec.toFloat() / 1000f)
            )
        }
    }

    fun handlePlaybackState(data: SpotifyPlaybackStateChangedData){
        val position = data.positionInMs.toFloat() / 1000f
        updateUiState(homeUiState.value.copy(isPlaying = data.playing, position = position))
    }

    fun resume(){
        viewModelScope.launch {
            spotifyService.resume()
        }
    }

    fun pause(){
        viewModelScope.launch {
            spotifyService.pause()
        }
    }

    fun next(){
        viewModelScope.launch {
            spotifyService.next()
        }
    }

    fun previous(){
        viewModelScope.launch {
            spotifyService.previous()
        }
    }

    fun changePosition(position: Float) {
        viewModelScope.launch {
            spotifyService.setPositionAndResume(position)
        }
        updateUiState(homeUiState.value.copy(position = position))
    }

    fun updateProgress(){
        if(spotifyService != null) {
            viewModelScope.launch {
                val position = spotifyService.getCurrentProgress()?.div(1000f)
                updateUiState(homeUiState.value.copy(position = position!!))
            }
        }
    }

    fun getDevices(){
//        viewModelScope.launch {
            spotifyService.getDevices()
//        }
    }
}

data class HomeUiState(
    var image: SpotifyImage? = SpotifyImage(url=""),
    val name: String? = "",
    val authors: List<String?>? = listOf<String>(),
    val isPlaying: Boolean = false,
    val trackLength: Float = 0f,
    var position: Float = 0f
)