package com.example.tunez.viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.adamratzman.spotify.models.SpotifyImage
import com.adamratzman.spotify.models.Track
import com.example.tunez.activities.NavBarItems
import com.example.tunez.activities.Routes
import com.example.tunez.content.Playlist
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class NavControllerViewModel: ViewModel()  {
//    private val _navController = MutableStateFlow<NavController?>(null)
//    val navController: StateFlow<NavController?> = _navController.asStateFlow()
    private var _uiState = MutableStateFlow(NavUiState())
    val navUiState: StateFlow<NavUiState> = _uiState.asStateFlow()

    fun setNavController(controller: NavHostController) {
        _uiState.update { it.copy(controller = controller)}
        Log.i("NavVM", controller.toString())
        Log.i("NavVM", navUiState.value.controller.toString())

    }

    fun goBack(){
        Log.i("NavVM", navUiState.value.controller.toString())
        navUiState.value.controller?.popBackStack()
//        navUiState.value.controller?.navigate(Routes.Profile.route) {
//            launchSingleTop = true
//            restoreState = false
//        }
    }
    fun goToPlaylist(playlist: Playlist){
        var image: String? = "nullable"
        if(playlist.image != null){
            image = playlist.image
        }
        Log.i("NavVM", image!!)
        val tracks = Json.encodeToString(playlist.tracks)
        navUiState.value.controller?.navigate(Routes.Playlist.route + "?name=${playlist.name}&durationInMs=${playlist.durationInMs}&image=${image!!}&tracks=${tracks}&id=${playlist.id}"){}
    }
    fun goToAddPlaylist(){
        navUiState.value.controller?.navigate(Routes.AddPlaylist.route)
    }

    fun goToChoosePlaylist(track: Track){
//        val trackSer = Gson().toJson(track)
        navUiState.value.controller?.navigate(Routes.ChoosePlaylist.route + "?uri=${track.uri.uri}")
    }
}

data class NavUiState(
    var controller: NavHostController? = null
)