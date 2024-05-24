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
import kotlinx.coroutines.launch

class HomeViewModel(val spotifyService: SpotifyService, private val application: SpotifyPlaygroundApplication): AndroidViewModel(application) {
//    var name by mutableStateOf("")
//    var authors by mutableStateOf(listOf<String>())
//    var isPlaying by mutableStateOf(false)
    private val context: Context
        get() = getApplication<SpotifyPlaygroundApplication>().applicationContext
    private val receiver = SpotifyBroadcastReceiver(this)
    private var _uiState = MutableStateFlow(HomeUiState())
    val homeUiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
//    var homeUiState by mutableStateOf(HomeUiState())
//        private set
    fun updateUiState(uiState: HomeUiState){
        _uiState.value = uiState
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
            updateUiState(homeUiState.value.copy(position = position))
        }
    }
}

data class HomeUiState(
    var image: SpotifyImage? = SpotifyImage(url=""),
    val name: String? = "",
    val authors: List<String?>? = listOf<String>(),
    val isPlaying: Boolean = false,
    val trackLength: Float = 0f,
    val position: Float = 0f
)