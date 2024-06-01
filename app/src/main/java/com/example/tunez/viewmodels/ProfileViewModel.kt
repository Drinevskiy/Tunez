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
import com.example.tunez.screens.millisecondsToHoursAndMinutes
import com.example.tunez.ui.service.SpotifyService
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.NullPointerException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class ProfileViewModel(val spotifyService: SpotifyService): ViewModel() {
    private var _uiState = MutableStateFlow(ProfileUiState())
    val profileUiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
//        getAllInfo()
        getFavouriteTracks()
        getPlaylists()
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
                playlists = uiState.playlists
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
                    image = url,
                    id = "favourite")
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
        var favTracks = listOf<String>()
        var duration = 0
        val name = "Favourite Tracks"
        viewModelScope.launch {
            try {
                if (user != null) {
                    val db = Firebase.database.reference
                    val playlist = db.child("Users")
                        .child(user!!.uid)
                        .child("favouritePlaylist")
                    playlist.get().addOnCompleteListener {
                        Log.i("firebase", "success getting playlist")
                        favTracks =
                            it.result.child("favouriteTracks").value as? List<String> ?: emptyList()
                        Log.i("firebase", favTracks.toString())
                        duration = it.result.child("duration").value.toString().toIntOrNull() ?: 0
                        playlist.setValue(
                            mapOf(
                                "favouriteTracks" to favTracks,
                                "name" to name,
                                "duration" to duration
                            )
                        )
                        loadFavouriteImage()
                        updateUiState(
                            profileUiState.value.copy(
                                favouritePlaylist =
                                Playlist(
                                    durationInMs = duration,
                                    name = name,
                                    tracks = favTracks,
                                    image = profileUiState.value.favouritePlaylist.image,
                                    id = "favourite"
                                )
                            )
                        )
                    }

                    .addOnFailureListener {
                        Log.e("firebase", "error getting playlist")
                    }
            }
            } catch (ex: Exception) {
                Log.e("firebase", "Error getting/setting data", ex)
            }
        }
    }

    suspend fun removeFromFavouriteTracks(track: Track){
        val db = Firebase.database.reference
        var favTracks = listOf<String>()
        var duration = 0
        val playlistNode = db.child("Users")
            .child(Firebase.auth.currentUser!!.uid)
            .child("favouritePlaylist")
        // Для обновления uiState после выполнения удаления из бд
        return suspendCancellableCoroutine { continuation ->
            playlistNode.get().addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.i("firebase", "success getting playlist")
                    favTracks = it.result.child("favouriteTracks").value as? List<String>
                        ?: emptyList()
                    favTracks = favTracks.minus(track.uri.uri)
                    Log.i("firebase", favTracks.toString())
                    duration = it.result.child("duration").value.toString().toIntOrNull() ?: 0
                    duration -= track.length
                    playlistNode.setValue(
                        mapOf(
                            "favouriteTracks" to favTracks,
                            "name" to "Favourite Tracks",
                            "duration" to duration
                        )
                    ).addOnCompleteListener {
                        continuation.resume(Unit)
                    }
                } else {
                    continuation.resumeWithException(
                        it.exception ?: RuntimeException("Unknown error")
                    )
                }
                updateUiState(
                    profileUiState.value.copy(
                        favouritePlaylist =
                        Playlist(
                            durationInMs = duration,
                            name = "Favourite Tracks",
                            tracks = favTracks,
                            image = profileUiState.value.favouritePlaylist.image,
                            id = "favourite"
                        )
                    )
                )
            }
        }
    }

    suspend fun loadImage(url: String): String? {
        return withContext(viewModelScope.coroutineContext) {
            var imageUrl: String? = null
            try {
                imageUrl = getTrackFromUri(url)?.album?.images?.get(0)?.url
            } catch (ex: IndexOutOfBoundsException) {
                Log.e("ProfileViewModel", "Track list is empty")
            }
            imageUrl
        }
    }
    fun getPlaylists(){
        viewModelScope.launch {
            if(user != null){
                val db = Firebase.database.reference
                var playlists = listOf<Playlist>()
                val playlistsSnapshot = db.child("Users")
                    .child(user!!.uid)
                    .child("playlists")
                playlistsSnapshot.get().addOnCompleteListener {
                    for (children in it.result.children) {
                        val playlist = children.value as? Map<String, Any> ?: mapOf()
                        val duration = playlist["duration"].toString().toIntOrNull() ?: 0
                        val name = playlist["name"]?.toString() ?: "No name"
                        val tracks = playlist["tracks"] as? List<String> ?: listOf()
                        val id = children.key.toString()
                        var image: String? = null
                        viewModelScope.launch {
                            if (tracks.isNotEmpty()) {
                                supervisorScope {
                                    image = loadImage(tracks[0])
                                    Log.i("firebase", image.toString())
                                }
                            }
                                playlists = playlists.plus(
                                    Playlist(
                                        durationInMs = duration,
                                        name = name,
                                        tracks = tracks,
                                        image = image,
                                        id = id
                                    )
                                )
                                updateUiState(
                                    profileUiState.value.copy(
                                        playlists = playlists
                                    )
                                )

                        }
                        }
                }
            }
        }
    }
    fun addPlaylist(name: String){
        viewModelScope.launch {
            val db = Firebase.database.reference
            val tracks = listOf<String>()
            val duration = 0
            val id = db.child("Users")
                .child(user!!.uid)
                .child("playlists")
                .push()
            id.setValue(
                        mapOf(
                            "name" to name,
                            "duration" to duration,
                            "tracks" to tracks
                        )
                )
            val playlist = Playlist(
                durationInMs =  duration,
                name = name,
                tracks = tracks,
                id = id.key
            )
            updateUiState(
                profileUiState.value.copy(
                    playlists = profileUiState.value.playlists.plus(playlist)
                )
            )
        }
    }

    fun addTrackToPlaylist(uri: String, playlist: Playlist) {
        var tracks = listOf<String>()
        var duration = 0
        try {
            if (user != null) {
                val db = Firebase.database.reference
                val playlistSnapshot = db.child("Users")
                    .child(user!!.uid)
                    .child("playlists")
                    .child(playlist.id!!)
                playlistSnapshot.get().addOnCompleteListener {
                    Log.i("firebase", "success getting playlist")
                    tracks = it.result.child("tracks").value as? List<String> ?: emptyList()
                    duration = it.result.child("duration").value.toString().toIntOrNull() ?: 0
                    Log.i("firebase", tracks.toString())
                    viewModelScope.launch {
                        if (uri !in tracks) {
                            tracks = tracks.plus(uri)
                            duration += spotifyService.stringUriToTrack(uri)?.length!!
                        }
                        playlistSnapshot.setValue(
                            mapOf(
                                "tracks" to tracks,
                                "name" to playlist.name,
                                "duration" to duration,
                            )
                        )
                        val index = profileUiState.value.playlists.indexOf(profileUiState.value.playlists.filter { pl -> pl.id == playlist.id }[0])
                        val tempList = profileUiState.value.playlists.toMutableList()
                        Log.i("ProfileViewModel", "First: $playlist")
                        supervisorScope {
                            playlist.image = loadImage(tracks[0])
                            Log.i("ProfileViewModel", "Second $playlist")

                        }
                        tempList[index] = playlist
                        Log.i("ProfileViewModel", "Third $playlist")

                        updateUiState(
                            profileUiState.value.copy(
                                playlists = tempList
                            )
                        )
                    }
                }
                    .addOnFailureListener {
                        Log.e("firebase", "error getting playlist")
                    }
            }
        } catch (ex: Exception) {
            Log.e("firebase", "Error getting/setting data", ex)
        }
    }

    suspend fun removeTrackFromPlaylist(track: Track, playlist: Playlist) {
        val db = Firebase.database.reference
        var tracks = listOf<String>()
        var duration = 0
        if(user != null) {
            val playlistNode = db.child("Users")
                .child(user!!.uid)
                .child("playlists")
                .child(playlist.id!!)
            // Для обновления uiState после выполнения удаления из бд
            return suspendCancellableCoroutine { continuation ->
                playlistNode.get().addOnCompleteListener {
                    if (it.isSuccessful) {
                        tracks = it.result.child("tracks").value as? List<String> ?: emptyList()
                        tracks = tracks.minus(track.uri.uri)
                        Log.i("firebase", "Remove from playlist " + tracks.toString())
                        duration = it.result.child("duration").value.toString().toIntOrNull() ?: 0
                        duration -= track.length
                        playlistNode.setValue(
                            mapOf(
                                "tracks" to tracks,
                                "name" to playlist.name,
                                "duration" to duration
                            )
                        ).addOnCompleteListener {
                            continuation.resume(Unit)
                        }
                    } else {
                        continuation.resumeWithException(
                            it.exception ?: RuntimeException("Unknown error")
                        )
                    }
                    val index = profileUiState.value.playlists.indexOf(profileUiState.value.playlists.filter { pl -> pl.id == playlist.id }[0])
                    val tempList = profileUiState.value.playlists.toMutableList()
                    tempList[index] = playlist
                    updateUiState(
                        profileUiState.value.copy(
                            playlists = tempList
                        )
                    )
                }
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
    val playlists: List<Playlist> = listOf()
)