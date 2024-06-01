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
//                    PlaylistScreen(playlist, myApplication.spotifyService, this)
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

