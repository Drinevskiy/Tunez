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
import com.example.tunez.activities.NavBarItems
import com.example.tunez.activities.Routes
import com.example.tunez.content.Playlist
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

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

    fun goToProfile(){
        Log.i("NavVM", navUiState.value.controller.toString())
        navUiState.value.controller?.navigate(Routes.Profile.route){
//            popUpTo(NavBarItems.BarItems[4].route)
//            restoreState = false
        }
    }
    fun goToPlaylist(playlist: Playlist){
        Log.i("NavVM", navUiState.value.controller.toString())
        val playlistSerialized = Gson().toJson(playlist.name)
        Log.i("NavVM", playlistSerialized)
        navUiState.value.controller?.navigate(Routes.Playlist.route + "?name=${playlist.name}&durationInMs=${playlist.durationInMs}&image=${playlist.image}&tracks=${playlist.tracks.joinToString(",")}"){
//        navUiState.value.controller?.navigate(Routes.Playlist.route + "/$playlistSerialized"){
//            popUpTo(NavBarItems.BarItems[4].route)
//            restoreState = false
        }
    }
}

data class NavUiState(
    var controller: NavHostController? = null
)