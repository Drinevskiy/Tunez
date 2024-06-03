package com.example.tunez.viewmodels

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.adamratzman.spotify.models.SpotifyImage
import com.adamratzman.spotify.notifications.SpotifyBroadcastType
import com.adamratzman.spotify.notifications.SpotifyMetadataChangedData
import com.adamratzman.spotify.notifications.SpotifyPlaybackStateChangedData
import com.adamratzman.spotify.notifications.registerSpotifyBroadcastReceiver
import com.example.tunez.SpotifyPlaygroundApplication
import com.example.tunez.ui.service.SpotifyBroadcastReceiver
import com.example.tunez.ui.service.SpotifyService
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.MutableData
import com.google.firebase.database.Transaction
import com.google.firebase.database.database
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.WeekFields
import java.util.Locale

val today = LocalDate.now().toString()
//val day = LocalDate.of(2024,5,30).toString()
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
        Log.i("SpotifyService", "Handle Metadata " + data.trackName)
        viewModelScope.launch {
            val track = spotifyService.stringUriToTrack(data.playableUri.uri)
            Log.i("SpotifyService", "Handle Metadata " + data.trackName)
            updateUiState(
                homeUiState.value.copy(
                    image = track.album.images?.get(0),
                    name = data.trackName,
                    authors = listOf(data.artistName),
                    trackLength = data.trackLengthInSec.toFloat() / 1000f
                )
            )
            // Увеличение количества прослушиваний на 1
            val date = Firebase.database.reference.child("Chart").child(today)
            date.runTransaction(object : Transaction.Handler{
                override fun doTransaction(currentDate: MutableData): Transaction.Result {
                    val tracks = currentDate.value as? List<Map<String, Long>> ?: emptyList()
                    val index = tracks.indexOf(tracks.firstOrNull { it.containsKey(data.playableUri.uri) })
                    var newTracks = tracks.toMutableList()
                    if(index < 0){
                        newTracks = newTracks.plus(mapOf(data.playableUri.uri to 1L)).toMutableList()
                    } else {
                        var count = newTracks[index][data.playableUri.uri] ?: 0L
                        count++
                        newTracks[index] = mapOf(data.playableUri.uri to count)
                    }
                    currentDate.value = newTracks
                    return Transaction.success(currentDate)
                }
                override fun onComplete(
                    error: DatabaseError?,
                    committed: Boolean,
                    currentData: DataSnapshot?
                ) {
                    if (error != null) {
                        Log.e("Firebase", "Transaction failed: ${error.message}")
                    } else if (committed) {
                        Log.d("Firebase", "Transaction success: ${currentData?.value}")
                    } else {
                        Log.d("Firebase", "Transaction not committed")
                    }
                }
            })
//                override fun doTransaction(currentData: MutableData): Transaction.Result {
//                    Log.i("Chart", currentData.toString())
//
//                    val currentCount = currentData.getValue(Int::class.java) ?: 0
//                    currentData.value = currentCount + 1
//                    return Transaction.success(currentData)
//                }
//
//                override fun onComplete(
//                    error: DatabaseError?,
//                    committed: Boolean,
//                    currentData: DataSnapshot?
//                ) {
//                    if (error != null) {
//                        Log.e("Firebase", "Transaction failed: ${error.message}")
//                    } else if (committed) {
//                        Log.d("Firebase", "Transaction success: ${currentData?.value}")
//                    } else {
//                        Log.d("Firebase", "Transaction not committed")
//                    }
//                }
//            })
        }
    }

    fun handlePlaybackState(data: SpotifyPlaybackStateChangedData){
        val position = data.positionInMs.toFloat() / 1000f
        Log.i("SpotifyService", "Handle playback state " + data.playing.toString())
        updateUiState(homeUiState.value.copy(isPlaying = data.playing, position = position))
    }

    fun checkPlayback(){
        viewModelScope.launch {
            val devices = spotifyService.getDevices()
            if (devices.isNullOrEmpty()) {
                Log.i("SpotifyService", "Reset playback")
                updateUiState(homeUiState.value.copy(isPlaying = false, position = 0f))
            }
            else{
                Log.i("SpotifyService", "Not Reset playback")
            }
        }
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
                var position = 0f
                try {
                    val progress = spotifyService.getCurrentProgress()//?.div(1000f)!!
                    Log.i("SpotifyService", progress.toString())
                    position = progress?.div(1000f)!!
                }
                catch (ex: NullPointerException){
                    Log.e("SpotifyService", "Error getting current progress")
                }
                updateUiState(homeUiState.value.copy(position = position))
            }
        }
    }

    fun getDevices(){
//        viewModelScope.launch {
//            spotifyService.getDevices()
//        }
    }
}

data class HomeUiState(
    var image: SpotifyImage? = null,//SpotifyImage(url=""),
    val name: String? = "",
    val authors: List<String?>? = listOf<String>(),
    val isPlaying: Boolean = false,
    val trackLength: Float = 0f,
    var position: Float = 0f
)