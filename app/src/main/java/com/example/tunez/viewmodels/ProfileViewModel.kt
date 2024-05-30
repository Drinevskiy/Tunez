package com.example.tunez.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adamratzman.spotify.models.Track
import com.adamratzman.spotify.models.toPlayableUri
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import com.example.tunez.activities.user
import com.example.tunez.content.Playlist
import com.example.tunez.ui.service.SpotifyService
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.NullPointerException

class ProfileViewModel(val spotifyService: SpotifyService): ViewModel() {
    private var _uiState = MutableStateFlow(ProfileUiState())
    val profileUiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
//        getAllInfo()
        getFavouriteTracks()
        Log.i("ProfileViewModel", profileUiState.value.favouritePlaylist.toString())

    }
    fun updateUiState(uiState: ProfileUiState){
        _uiState.update {
            it.copy(
                user = uiState.user,
                uid = uiState.uid,
                username = uiState.username,
                role = uiState.role,
                email = uiState.email,
                genres = uiState.genres,
                favouritePlaylist = uiState.favouritePlaylist,
            )
        }
    }
    fun getAllInfo(){
        getUid()
        getRole()
        getUsername()
        getEmail()
        getGenres()
    }
    fun getUid(){
        if(user != null) {
            val uid = Firebase.database.reference.child("Users").child(profileUiState.value.user!!.uid)
            updateUiState(profileUiState.value.copy(uid = uid))
        }
    }
    fun getRole(){
        if(user != null) {
            profileUiState.value.uid?.child("role")?.get()?.addOnSuccessListener {
                val role = it.value.toString()
                updateUiState(profileUiState.value.copy(role = role))
            }?.addOnFailureListener {
                Log.e("firebase", "Error getting data", it)
            }
        }
    }
    fun getUsername(){
        if(user != null) {
            profileUiState.value.uid?.child("username")?.get()?.addOnSuccessListener {
                val username = it.value.toString()
                updateUiState(profileUiState.value.copy(username = username))
            }?.addOnFailureListener {
                Log.e("firebase", "Error getting data", it)
            }
        }
    }
    fun getEmail(){
        if(user != null) {
            profileUiState.value.uid?.child("email")?.get()?.addOnSuccessListener {
                val email = it.value.toString()
                updateUiState(profileUiState.value.copy(email = email))
            }?.addOnFailureListener {
                Log.e("firebase", "Error getting data", it)
            }
        }
    }
    fun getGenres(){
        if(user != null) {
            profileUiState.value.uid?.child("genres")?.get()?.addOnSuccessListener {
                val genres = it.value as List<String>
                updateUiState(profileUiState.value.copy(genres = genres))
            }?.addOnFailureListener {
                Log.e("firebase", "Error getting data", it)
            }
        }
    }
//    suspend fun uriToTrack(uri: String): Track? {
//        return withContext(viewModelScope.coroutineContext) {
//            spotifyService.stringUriToTrack(uri)
//        }
//    }
    fun loadFavouriteImage() {
        viewModelScope.launch {
            try {
                val url = getTrackFromUri(profileUiState.value.favouritePlaylist.tracks.get(0))?.album?.images?.get(0)?.url
                updateUiState(profileUiState.value.copy(favouritePlaylist =
                Playlist(
                    durationInMs = profileUiState.value.favouritePlaylist.durationInMs,
                    name = profileUiState.value.favouritePlaylist.name,
                    tracks = profileUiState.value.favouritePlaylist.tracks,
                    image = url)
                ))
            }
            catch (ex: IndexOutOfBoundsException){
                Log.e("ProfileViewModel", "Track list is empty")
            }
        }
    }

    private suspend fun getTrackFromUri(uri: String): Track? {
        return withContext(viewModelScope.coroutineContext) {
            spotifyService.stringUriToTrack(uri)
        }
    }
    fun getFavouriteTracks(){
        if(user != null) {
            viewModelScope.launch {
                val db = Firebase.database.reference
//                var tracksUri: List<String> = listOf()
                val snapshot = db.child("Users")
                    .child(Firebase.auth.currentUser!!.uid)
                    .child("favouritePlaylist")
                    .child("favouriteTracks").get().await()
                val tracksUri = snapshot.value as? List<String> ?: emptyList()
                val durationSnapshot = db.child("Users")
                    .child(Firebase.auth.currentUser!!.uid)
                    .child("favouritePlaylist")
                    .child("duration").get().await()
                var duration = durationSnapshot.value?.toString()?.toIntOrNull() ?: 0
                val nameSnapshot = db.child("Users")
                    .child(Firebase.auth.currentUser!!.uid)
                    .child("favouritePlaylist")
                    .child("name").get().await()
                var name = nameSnapshot.value.toString() ?: ""
                Log.i("ProfileViewModel", "Name: " + name)
                Log.i("ProfileViewModel", "Duration: " + duration.toString())
                Log.i("ProfileViewModel", tracksUri.toString())
                loadFavouriteImage()

//                Log.i("ProfileViewModel", profileUiState.value.favouritePlaylist.image.toString())
                updateUiState(profileUiState.value.copy(favouritePlaylist =
                Playlist(
                    durationInMs = duration,
                    name = name,
                    tracks = tracksUri,
                    image = profileUiState.value.favouritePlaylist.image)
                ))
//                Log.i("ProfileViewModel", profileUiState.value.favouritePlaylist.image.toString())

//                updateUiState(profileUiState.value.copy(favouritePlaylist =
//                Playlist(
//                    durationInMs = duration,
//                    name = name,
//                    tracks = tracksUri,
//                    image = profileUiState.value.favouritePlaylist.image)
//                ))
            }
        }
    }


    fun getAllUsers(){
        if(profileUiState.value.role == "admin"){
            Firebase.database.reference.child("Users").get()
                .addOnSuccessListener { dataSnapshot ->
                    if (dataSnapshot.exists()) {
                        // Iterate through the children (users) and get their values
                        for (childSnapshot in dataSnapshot.children) {
                            val user = childSnapshot.getValue()
                            // Do something with the user data
//                            user.
                            Log.i("firebase", user.toString())
                        }
                    } else {
                        // The "Users" node is empty
                        println("No users found.")
                    }
                }
                .addOnFailureListener { error ->
                    Log.e("firebase", "Node not found")
                }
        }
    }
}

data class ProfileUiState(
    val user: FirebaseUser? = com.example.tunez.activities.user,
    val username: String = "",
    val role: String = "",
    val email: String = "",
    val uid: DatabaseReference? = null,
    val genres: List<String> = listOf(),
    val favouritePlaylist: Playlist = Playlist(),
)