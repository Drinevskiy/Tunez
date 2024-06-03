package com.example.tunez.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adamratzman.spotify.models.PlayableUri
import com.adamratzman.spotify.models.Track
import com.example.tunez.activities.user
import com.example.tunez.content.Playlist
import com.example.tunez.roles.IAccount
import com.example.tunez.ui.service.SpotifyService
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class ProfileViewModel(val spotifyService: SpotifyService): ViewModel() {
    private var _uiState = MutableStateFlow(ProfileUiState())
    val profileUiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        getAllInfo()
    }

    fun updateUiState(user: IAccount) {
        _uiState.update {
            it.copy(
                user = user,
            )
        }
    }

    fun updateAllUsers(users: List<UserInfo>) {
        _uiState.update {
            it.copy(
                allUsers = users,
            )
        }
    }

    private fun updateUserForAdmin(user: IAccount) {
        _uiState.update {
            it.copy(
                currentUserForAdmin = user
            )
        }
    }

    fun getAllInfo() {
        if (user != null) {
            viewModelScope.launch {
                val db = Firebase.database.reference.child("Users").child(user!!.uid)
                val uid = db.key
                var role = "user"
                var username: String? = null
                var email: String? = null
                var genres: List<String> = listOf()
                var playlists: List<Playlist> = listOf()
                db.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            email = dataSnapshot.child("email").value.toString()
                            role = dataSnapshot.child("role").value.toString()
                            username = dataSnapshot.child("username").value.toString()
                            genres = dataSnapshot.child("genres").value as? List<String> ?: emptyList()
                            updateUiState(
                                user =
                                IAccount(
                                    uid = uid,
                                    username = username,
                                    email = email,
                                    role = role,
                                    genres = genres,
                                    favouritePlaylist = profileUiState.value.user.favouritePlaylist,
                                    playlists = profileUiState.value.user.playlists
                                )
                            )
                            getPlaylists()
                            getFavouriteTracks()
                            if(profileUiState.value.user.role == "admin"){
                                Firebase.database.reference.child("Users").get().addOnCompleteListener { dataSnapshot2 ->
                                    var allUsers = listOf<UserInfo>()
                                    for(children in dataSnapshot2.result.children){
                                        val uid2 = children.key
                                        val email2 = children.child("email").value.toString()
                                        val role2 = children.child("role").value.toString()
                                        val username2 = children.child("username").value.toString()
                                        val item = UserInfo(uid2, username2, email2, role2)
                                        allUsers = allUsers.plus(item)
                                    }
                                    updateAllUsers(allUsers)
                                }
                            }
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        Log.e("firebase", "Error gettin profile info")
                    }
                })
            }
        }
    }

    private suspend fun getTrackFromUri(uri: String): Track? {
        return withContext(viewModelScope.coroutineContext) {
            spotifyService.stringUriToTrack(uri)
        }
    }

    fun getFavouriteTracks() {
        var favTracks = listOf<String>()
        var duration = 0
        val name = "Favourite Tracks"
        viewModelScope.launch {
            try {
                if (user != null) {
                    val db = Firebase.database.reference
                    val playlist = db.child("Users")
                        .child(profileUiState.value.user.uid!!)
                        .child("favouritePlaylist")
                    playlist.get().addOnCompleteListener {
                        Log.i("firebase", "success getting playlist")
                        favTracks =
                            it.result.child("favouriteTracks").value as? List<String> ?: emptyList()
                        duration = it.result.child("duration").value.toString().toIntOrNull() ?: 0
                        Log.i("firebase", favTracks.toString())
//                        if(favTracks.isNotEmpty()) {
                        playlist.setValue(
                            mapOf(
                                "favouriteTracks" to favTracks,
                                "name" to name,
                                "duration" to duration
                            )
                        )
//                        }
                        viewModelScope.launch {
                            val tracks = spotifyService.stringUrisToTracks(favTracks)
                            var image: String? = null
                            supervisorScope {
                                if (favTracks.isNotEmpty()) {
                                    image = loadImage(favTracks[0])
                                }
                                Log.i("firebase", image.toString())
                            }
                            updateUiState(
                                user =
                                IAccount(
                                    uid = profileUiState.value.user.uid,
                                    username = profileUiState.value.user.username,
                                    email = profileUiState.value.user.email,
                                    role = profileUiState.value.user.role,
                                    genres = profileUiState.value.user.genres,
                                    favouritePlaylist = Playlist(
                                        durationInMs = duration,
                                        name = name,
                                        tracks = tracks,
                                        image = image,
                                        id = "favourite"
                                    ),
                                    playlists = profileUiState.value.user.playlists
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
    }


    fun addToFavouriteTracks(track: Track){
        var favTracks = listOf<String>()
        var duration = 0
        if(user != null) {
            viewModelScope.launch {
                try {
                    val db = Firebase.database.reference
                    val playlist = db.child("Users")
                        .child(profileUiState.value.user.uid!!)
                        .child("favouritePlaylist")
                    playlist.get().addOnCompleteListener {
                        Log.i("firebase", "success getting playlist")
                        favTracks = it.result.child("favouriteTracks").value as? List<String> ?: emptyList()
                        duration = it.result.child("duration").value.toString().toIntOrNull() ?: 0
                        Log.i("firebase", favTracks.toString())
                        viewModelScope.launch {
                            if (track.uri.uri !in favTracks) {
                                favTracks = favTracks.plus(track.uri.uri)
                                duration += spotifyService.stringUriToTrack(track.uri.uri).length
                            }
                            playlist.setValue(
                                mapOf(
                                    "favouriteTracks" to favTracks,
                                    "name" to "Favourite Tracks",
                                    "duration" to duration
                                )
                            )
                            val tracks = spotifyService.stringUrisToTracks(favTracks)
                            var image: String? = null
                            if (tracks.isNotEmpty()) {
                                supervisorScope {
                                    image = loadImage(favTracks[0])
                                }
                            }
                            updateUiState(user =
                                IAccount(
                                    uid = profileUiState.value.user.uid,
                                    username = profileUiState.value.user.username,
                                    email = profileUiState.value.user.email,
                                    role = profileUiState.value.user.role,
                                    genres = profileUiState.value.user.genres,
                                    favouritePlaylist = Playlist(
                                        durationInMs = duration,
                                        name = profileUiState.value.user.favouritePlaylist.name,
                                        tracks = tracks,
                                        image = image,
                                        id = "favourite"
                                    ),
                                    playlists = profileUiState.value.user.playlists
                                )
                            )
                            makeToast("Track added to Favourite Tracks")
                        }
                    }
                    .addOnFailureListener {
                        Log.e("firebase", "error getting playlist")
                    }
                } catch (ex: Exception) {
                    Log.e("firebase", "Error getting/setting data", ex)
                }
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
                            "name" to profileUiState.value.user.favouritePlaylist.name,
                            "duration" to duration
                        )
                    ).addOnCompleteListener {
                        continuation.resume(Unit)
                    }
                    viewModelScope.launch {
                        var image: String? = null
                        val tracks = spotifyService.stringUrisToTracks(favTracks)
                        if (tracks.isNotEmpty()) {
                            supervisorScope {
                                image = loadImage(favTracks[0])
                            }
                        }
                        updateUiState(user =
                            IAccount(
                                uid = profileUiState.value.user.uid,
                                username = profileUiState.value.user.username,
                                email = profileUiState.value.user.email,
                                role = profileUiState.value.user.role,
                                genres = profileUiState.value.user.genres,
                                favouritePlaylist = Playlist(
                                    durationInMs = duration,
                                    name = profileUiState.value.user.favouritePlaylist.name,
                                    tracks = tracks,
                                    image = image,
                                    id = "favourite"
                                ),
                                playlists = profileUiState.value.user.playlists
                            )
                        )
                    }
                } else {
                    continuation.resumeWithException(
                        it.exception ?: RuntimeException("Unknown error")
                    )
                }
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
            if (user != null) {
                val db = Firebase.database.reference
                var playlists = listOf<Playlist>()
                val playlistsSnapshot = db.child("Users")
                    .child(profileUiState.value.user.uid!!)
                    .child("playlists")
                playlistsSnapshot.get().addOnCompleteListener {
                    if(it.isSuccessful) {
                        if (it.result.hasChildren()) {
                            for (children in it.result.children) {
                                val playlist = children.value as? Map<String, Any> ?: mapOf()
                                val duration = playlist["duration"].toString().toIntOrNull() ?: 0
                                val name = playlist["name"]?.toString() ?: "No name"
                                val tracksDb = playlist["tracks"] as? List<String> ?: listOf()
                                val id = children.key.toString()
                                var image: String? = null
                                viewModelScope.launch {
                                    if (tracksDb.isNotEmpty()) {
                                        supervisorScope {
                                            image = loadImage(tracksDb[0])
                                            Log.i("firebase", image.toString())
                                        }
                                    }
                                    var tracks: List<Track?> = listOf()
                                    if (tracksDb.isNotEmpty()) {
                                        tracks = spotifyService.stringUrisToTracks(tracksDb)
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
                                    Log.i("ProfileViewModel", profileUiState.value.user.username!!)
                                    updateUiState(
                                        user =
                                        IAccount(
                                            uid = profileUiState.value.user.uid,
                                            username = profileUiState.value.user.username,
                                            email = profileUiState.value.user.email,
                                            role = profileUiState.value.user.role,
                                            genres = profileUiState.value.user.genres,
                                            favouritePlaylist = profileUiState.value.user.favouritePlaylist,
                                            playlists = playlists
                                        )
                                    )
                                }
                            }
                        } else {
                            updateUiState(
                                user =
                                IAccount(
                                    uid = profileUiState.value.user.uid,
                                    username = profileUiState.value.user.username,
                                    email = profileUiState.value.user.email,
                                    role = profileUiState.value.user.role,
                                    genres = profileUiState.value.user.genres,
                                    favouritePlaylist = Playlist(),
                                    playlists = listOf()
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
                .child(profileUiState.value.user.uid!!)
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
                id = id.key
            )
            updateUiState(user =
                IAccount(
                    uid = profileUiState.value.user.uid,
                    username = profileUiState.value.user.username,
                    email = profileUiState.value.user.email,
                    genres = profileUiState.value.user.genres,
                    role = profileUiState.value.user.role,
                    favouritePlaylist = profileUiState.value.user.favouritePlaylist,
                    playlists = profileUiState.value.user.playlists.plus(playlist)
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
                    .child(profileUiState.value.user.uid!!)
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
                            duration += spotifyService.stringUriToTrack(uri).length
                        }
                        playlistSnapshot.setValue(
                            mapOf(
                                "tracks" to tracks,
                                "name" to playlist.name,
                                "duration" to duration,
                            )
                        )
                        val index = profileUiState.value.user.playlists.indexOf(profileUiState.value.user.playlists.filter { pl -> pl.id == playlist.id }[0])
                        val tempList = profileUiState.value.user.playlists.toMutableList()
                        Log.i("ProfileViewModel", "First: $playlist")
                        supervisorScope {
                            playlist.image = loadImage(tracks[0])
                            playlist.tracks = spotifyService.stringUrisToTracks(tracks)
                            playlist.durationInMs = duration
                            Log.i("ProfileViewModel", "Second $playlist")
                        }
                        tempList[index] = playlist
                        Log.i("ProfileViewModel", "Third $playlist")
                        updateUiState(user =
                            IAccount(
                                uid = profileUiState.value.user.uid,
                                username = profileUiState.value.user.username,
                                email = profileUiState.value.user.email,
                                role = profileUiState.value.user.role,
                                genres = profileUiState.value.user.genres,
                                favouritePlaylist = profileUiState.value.user.favouritePlaylist,
                                playlists = tempList.toList()
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
                .child(profileUiState.value.user.uid!!)
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
                    val index = profileUiState.value.user.playlists.indexOf(profileUiState.value.user.playlists.filter { pl -> pl.id == playlist.id }[0])
                    val tempList = profileUiState.value.user.playlists.toMutableList()
                    viewModelScope.launch {
                        supervisorScope {
                            playlist.image = null
                            if (tracks.isNotEmpty()){
                                playlist.image = loadImage(tracks[0])
                            }
                            playlist.tracks = spotifyService.stringUrisToTracks(tracks)
//                            playlist.durationInMs -= track.length
                            Log.i("ProfileViewModel", "Second $playlist")
                        }
                        if(index > -1) {
                            tempList[index] = playlist
                        }
                        updateUiState(user =
                            IAccount(
                                uid = profileUiState.value.user.uid,
                                username = profileUiState.value.user.username,
                                email = profileUiState.value.user.email,
                                role = profileUiState.value.user.role,
                                genres = profileUiState.value.user.genres,
                                favouritePlaylist = profileUiState.value.user.favouritePlaylist,
                                playlists = tempList.toList()
                            )
                        )
                    }
                }
            }
        }
    }
    fun deletePlaylist(playlist: Playlist) {
        viewModelScope.launch {
            val db = Firebase.database.reference
            db.child("Users")
                .child(profileUiState.value.user.uid!!)
                .child("playlists")
                .child(playlist.id!!).removeValue()
            updateUiState(user =
                IAccount(
                    uid = profileUiState.value.user.uid,
                    username = profileUiState.value.user.username,
                    email = profileUiState.value.user.email,
                    role = profileUiState.value.user.role,
                    genres = profileUiState.value.user.genres,
                    favouritePlaylist = profileUiState.value.user.favouritePlaylist,
                    playlists = profileUiState.value.user.playlists.minus(playlist)
                )
            )
        }
    }

    fun getAllUsers(){
        Log.i("ProfileViewModel", profileUiState.value.user.role!!)
        if(profileUiState.value.user.role == "admin"){
            val db = Firebase.database.reference
            db.child("Users").get().addOnCompleteListener { dataSnapshot ->
                var allUsers = listOf<UserInfo>()
                for(children in dataSnapshot.result.children){
                    val uid = children.key
                    val email = children.child("email").value.toString()
                    val role = children.child("role").value.toString()
                    val username = children.child("username").value.toString()
                    val item = UserInfo(uid, username, email, role)
                    Log.i("ProfileViewModel", item.toString())
                    allUsers = allUsers.plus(item)
                }
                Log.i("ProfileViewModel", allUsers.toString())
                updateAllUsers(allUsers)
            }
        }
    }

    fun play(uri: PlayableUri){
        viewModelScope.launch {
            spotifyService.play(uri)
        }
    }
    fun makeToast(message: String) {
        spotifyService.makeToast(message)
    }

    fun getInfoAboutUser(userInfo: UserInfo) {
        viewModelScope.launch {
            var playlists = listOf<Playlist>()
            var favouritePlaylist = Playlist()
            val db = Firebase.database.reference
            val playlistsSnapshot = db.child("Users")
                .child(userInfo.uid!!)
                .child("playlists")
            playlistsSnapshot.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (task.result.hasChildren()) {
                        for (children in task.result.children) {
                            val playlist = children.value as? Map<String, Any> ?: mapOf()
                            val duration = playlist["duration"].toString().toIntOrNull() ?: 0
                            val name = playlist["name"]?.toString() ?: "No name"
                            val tracksDb = playlist["tracks"] as? List<String> ?: listOf()
                            val id = children.key.toString()
                            var image: String? = null
                            viewModelScope.launch {
                                if (tracksDb.isNotEmpty()) {
                                    image = loadImage(tracksDb[0])
                                }
                                val tracks = spotifyService.stringUrisToTracks(tracksDb)
                                Log.i("ProfileViewModel", tracks.toString())

                                playlists = playlists.plus(
                                    Playlist(
                                        durationInMs = duration,
                                        name = name,
                                        tracks = tracks,
                                        image = image,
                                        id = id
                                    )
                                )
                                updateUserForAdmin(
                                    user =
                                    IAccount(
                                        uid = userInfo.uid,
                                        username = userInfo.username,
                                        email = userInfo.email,
                                        role = userInfo.role,
                                        playlists = playlists,
                                        favouritePlaylist = favouritePlaylist
                                    )
                                )
                                Log.i("ProfileViewModel", playlists.toString())
                            }
                        }
                    }
                }
                }
                val playlistF = db.child("Users")
                    .child(userInfo.uid!!)
                    .child("favouritePlaylist")
                playlistF.get().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val favTracks =
                            task.result.child("favouriteTracks").value as? List<String>
                                ?: emptyList()
                        val duration =
                            task.result.child("duration").value.toString().toIntOrNull() ?: 0
                        viewModelScope.launch {
                            val tracks = spotifyService.stringUrisToTracks(favTracks)
                            var image: String? = null
                            if (favTracks.isNotEmpty()) {
                                image = loadImage(favTracks[0])
                            }
                            favouritePlaylist = Playlist(
                                durationInMs = duration,
                                name = "Favourite Tracks",
                                tracks = tracks,
                                image = image,
                                id = "favourite"
                            )
                            updateUserForAdmin(
                                user =
                                IAccount(
                                    uid = userInfo.uid,
                                    username = userInfo.username,
                                    email = userInfo.email,
                                    role = userInfo.role,
                                    playlists = playlists,
                                    favouritePlaylist = favouritePlaylist
                                )
                            )
                        }
                    }
                }

        }
    }
    fun deleteUserPlaylist(user: IAccount, playlist: Playlist){
        viewModelScope.launch {
            val db = Firebase.database.reference
            db.child("Users")
                .child(user.uid!!)
                .child("playlists")
                .child(playlist.id!!).removeValue()

        }
    }

    suspend fun deleteTrackFromUserPlaylist(user: IAccount, track: Track, playlist: Playlist){
        val db = Firebase.database.reference
        var tracks = listOf<String>()
        var duration = 0
        val playlistNode = db.child("Users")
            .child(user.uid!!)
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
                val index = user.playlists.indexOf(user.playlists.filter { pl -> pl.id == playlist.id }[0])
                val tempList = user.playlists.toMutableList()
                viewModelScope.launch {
                    supervisorScope {
                        playlist.image = null
                        if (tracks.isNotEmpty()){
                            playlist.image = loadImage(tracks[0])
                        }
                        playlist.tracks = spotifyService.stringUrisToTracks(tracks)
                        Log.i("ProfileViewModel", "Second $playlist")
                    }
                    if(index > -1) {
                        tempList[index] = playlist
                    }
                    updateUserForAdmin(user =
                        IAccount(
                            uid = user.uid,
                            username = user.username,
                            email = user.email,
                            role = user.role,
                            playlists = tempList,
                            favouritePlaylist = user.favouritePlaylist
                        )
                    )
                }
            }
        }
    }

    suspend fun deleteTrackFromUserFavouriteTracks(user: IAccount, track: Track){
        val db = Firebase.database.reference
        var favTracks = listOf<String>()
        var duration = 0
        val playlistNode = db.child("Users")
            .child(user.uid!!)
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
                            "name" to user.favouritePlaylist.name,
                            "duration" to duration
                        )
                    ).addOnCompleteListener {
                        continuation.resume(Unit)
                    }
                    viewModelScope.launch {
                        var image: String? = null
                        val tracks = spotifyService.stringUrisToTracks(favTracks)
                        if (tracks.isNotEmpty()) {
                            supervisorScope {
                                image = loadImage(favTracks[0])
                            }
                        }
                        updateUserForAdmin(user =
                            IAccount(
                                uid = user.uid,
                                username = user.username,
                                email = user.email,
                                role = user.role,
                                playlists = user.playlists,
                                favouritePlaylist = Playlist(
                                    durationInMs = duration,
                                    name = user.favouritePlaylist.name,
                                    tracks = tracks,
                                    image = image,
                                    id = "favourite"
                                )
                            )
                        )
                    }
                } else {
                    continuation.resumeWithException(
                        it.exception ?: RuntimeException("Unknown error")
                    )
                }
            }
        }
    }

}

data class ProfileUiState(
    val user: IAccount = IAccount(),
    val allUsers: List<UserInfo> = listOf(),
    val currentUserForAdmin: IAccount = IAccount(),
)

data class UserInfo(
    var uid: String? = null,
    var username: String? = null,
    var email: String? = null,
    var role: String? = null,
)