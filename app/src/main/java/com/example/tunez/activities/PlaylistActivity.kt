package com.example.tunez.activities

import android.os.Bundle
import android.util.Log
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.adamratzman.spotify.models.PlayableUri
import com.adamratzman.spotify.models.Track
import com.example.tunez.SpotifyPlaygroundApplication
import com.example.tunez.content.Playlist
import com.example.tunez.screens.formatDuration
import com.example.tunez.ui.service.SpotifyService
import com.example.tunez.ui.theme.TunezTheme
import com.example.tunez.viewmodels.AppViewModelProvider
import com.example.tunez.viewmodels.NavControllerViewModel
import com.example.tunez.viewmodels.ProfileViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.database
import com.google.gson.Gson
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.koin.androidx.compose.inject
import kotlin.reflect.KSuspendFunction1

class PlaylistActivity(): BaseActivity() {
    val myApplication: SpotifyPlaygroundApplication
        get() = application as SpotifyPlaygroundApplication
//    vm: RecommendationsViewModel = viewModel(factory = AppViewModelProvider.Factory)

//    private val vmController: NavControllerViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val tracksUri = intent.getStringArrayListExtra("tracksUri") ?: emptyList()
        val serializedPlaylist = intent.getStringExtra("playlistSerialized")
        val playlist = Gson().fromJson(serializedPlaylist, Playlist::class.java)
        Log.i("duration", playlist.durationInMs.toString())
        setContent {
            TunezTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    PlaylistScreen(playlist, myApplication.spotifyService, this)
                }
            }
        }
//        var onBack:  LambdaWrapper<Unit, Unit>? = null
//        val serializedData = intent.getStringExtra("serializedFunction")
//        if (serializedData != null) {
//            onBack = Json.decodeFromString<LambdaWrapper<Unit, Unit>>(serializedData)
            // Используйте deserialized myObject
    }
//        val bundle = intent.extras
//        val onBack = bundle?.getSerializable("onBack") as? () -> Unit
//        val onBack = intent.getSerializableExtra("onBack") as? () -> Unit
//        val callback = onBackPressedDispatcher.addCallback(this) {
////            vmController.goToProfile()
//            Log.i("playlistActivity", NavBarItems.BarItems[4].route)
//
//        }
}

@Composable
fun PlaylistScreen(playlist: Playlist, spotifyService: SpotifyService, activity: BaseActivity, vm: ProfileViewModel = viewModel(factory = AppViewModelProvider.Factory)){
    val scope = rememberCoroutineScope()
    var tracks: List<Track> by remember { mutableStateOf(listOf())}
    var durationText by remember { mutableStateOf(millisecondsToHoursAndMinutes(playlist.durationInMs))}
    val vmController: NavControllerViewModel by inject()

    LaunchedEffect(Unit) {
        scope.launch {
            tracks = playlist.tracks.map { spotifyService.stringUriToTrack(it)!! }
        }
    }
    Column {
        Row(modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center) {
            IconButton(
                onClick = {
                    vmController.goToProfile()
//                    activity.onBackPressedDispatcher.onBackPressed()
                }
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = null,
                    modifier = Modifier.size(30.dp, 30.dp)
                )
            }
            Spacer(modifier = Modifier.weight(0.65f))
            Text(
                text = playlist.name,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                modifier = Modifier
                    .padding(0.dp, 10.dp)
            )
            Spacer(modifier = Modifier.weight(1f))
        }
        Text(
            text = durationText,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            modifier = Modifier
                .padding(bottom = 10.dp)
                .fillMaxWidth()
        )
        LazyVerticalGrid(
            columns = GridCells.Fixed(1),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(8.dp),
            modifier = Modifier.fillMaxWidth()
        ){
            items(tracks) {track ->
                TrackRow(track, scope, spotifyService::playTrack){
                    scope.launch {
                        val db = Firebase.database.reference
                        var favTracks = listOf<String>()
                        var duration = 0
//                        val durationSnapshot = db.child("Users")
//                            .child(Firebase.auth.currentUser!!.uid)
//                            .child("favouritePlaylist")
//                            .child("duration").get().await()
//                        var duration = durationSnapshot.value?.toString()?.toIntOrNull() ?: 0
//                        Log.i("firebase", "Duration 1: $duration")
//                        val snapshot = db.child("Users")
//                            .child(Firebase.auth.currentUser!!.uid)
//                            .child("favouritePlaylist")
//                            .child("favouriteTracks").get().await()
//                        favTracks = snapshot.value as? List<String> ?: emptyList()
//                        favTracks = favTracks.minus(track.uri.uri)
//                        duration -= track.length
//                        Log.i("firebase", "Duration 2: $duration")
//                        db.child("Users")
//                            .child(user!!.uid)
//                            .child("favouritePlaylist")
//                            .setValue(
//                                mapOf(
//                                    "favouriteTracks" to favTracks,
//                                    "name" to "Favourite Tracks",
//                                    "duration" to duration
//                                )
//                            )
                        val playlistNode = db.child("Users")
                            .child(Firebase.auth.currentUser!!.uid)
                            .child("favouritePlaylist")
                        playlistNode.get().addOnCompleteListener {
                            Log.i("firebase", "success getting playlist")
                            favTracks = it.result.child("favouriteTracks").value as? List<String>
                                ?: emptyList()
                            favTracks = favTracks.minus(track.uri.uri)
                            Log.i("firebase", favTracks.toString())
                            duration = it.result.child("duration").value.toString().toIntOrNull() ?: 0
                            scope.launch {
                                duration -= track.length
                                playlistNode.setValue(
                                    mapOf(
                                        "favouriteTracks" to favTracks,
                                        "name" to playlist.name,
                                        "duration" to duration
                                    )
                                )
                                durationText = millisecondsToHoursAndMinutes(duration)
                                tracks = tracks.minus(track)
                            }
                        }
                    }
                }
            }
        }
    }

}

@Composable
fun TrackRow(
    track: Track,
    scope: CoroutineScope,
    onClick: KSuspendFunction1<PlayableUri, Unit>,
    onDelete: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var tracksUri: List<String> = listOf()
    Row(modifier = Modifier
        .fillMaxWidth()
        .clickable {
            scope.launch {
                onClick.invoke(track.uri)
            }
        }) {
        GlideImage(
            imageModel =
            track.album.images?.get(0)?.url
                ?: "https://sun9-25.userapi.com/impg/Z3epnPuW1AG9bY8vNk6CxvPUfDC8Glje-nfRVA/tHFcX2ef9rk.jpg?size=900x900&quality=96&sign=27b00a943c3ac22fbaa34b00db97bea8&c_uniq_tag=DeuKuphk22jYBIyArxc3iAF8-bHFXuRzK_HtgZbSCrM&type=album",
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
//                            .fillMaxWidth()
                .height(65.dp)
                .width(65.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                Text(
                    text = track.name,
                    fontSize = 22.sp,
                    modifier = Modifier
                        .padding(bottom = 4.dp)
                )
                Text(
                    text = track.artists.map { it.name }.joinToString(", "),
                    fontSize = 17.sp,
                    modifier = Modifier
                )
            }
            IconButton(onClick = onDelete){
                Icon(imageVector = Icons.Filled.Delete, contentDescription = null)
            }
        }
    }
}

fun millisecondsToHoursAndMinutes(milliseconds: Int): String {
    val totalSeconds = milliseconds / 1000
    val hours = (totalSeconds / 3600).toInt()
    val minutes = ((totalSeconds % 3600) / 60).toInt()
    if(hours > 0) {
        return "$hours h. $minutes min."
    }
    return "$minutes min."
}