package com.example.tunez.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.adamratzman.spotify.models.PlayableUri
import com.adamratzman.spotify.models.Track
import com.example.tunez.content.Playlist
import com.example.tunez.ui.service.SpotifyService
import com.example.tunez.viewmodels.AppViewModelProvider
import com.example.tunez.viewmodels.NavControllerViewModel
import com.example.tunez.viewmodels.ProfileUiState
import com.example.tunez.viewmodels.ProfileViewModel
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.coroutines.launch
import org.koin.androidx.compose.inject
import kotlin.reflect.KSuspendFunction1

@Composable
fun PlaylistScreen(playlist: Playlist, spotifyService: SpotifyService) {
    val scope = rememberCoroutineScope()
    var tracks: List<Track?> by remember { mutableStateOf(playlist.tracks) }
    var durationText by remember { mutableStateOf(millisecondsToHoursAndMinutes (playlist.durationInMs)) }
    val vmController: NavControllerViewModel by inject()
    val vm: ProfileViewModel by inject()
    val uiState by vm.profileUiState.collectAsState()

//    LaunchedEffect(Unit) {
//        scope.launch {
//            tracks = playlist.tracks.map { spotifyService.stringUriToTrack(it)!! }
//        }
//    }
    Column {
        Row(modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center) {
            IconButton(
                onClick = {
                    vmController.goBack()
                }
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = null,
                    modifier = Modifier.size(30.dp, 30.dp)
                )
            }
            Spacer(modifier = Modifier.weight(0.5f))
            Column() {
                Text(
                    text = playlist.name,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    modifier = Modifier
                        .padding(0.dp, 10.dp)
                        .fillMaxWidth(0.65f)
                )
                Text(
                    text = durationText,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp,
                    modifier = Modifier
                        .padding(bottom = 10.dp)
                        .fillMaxWidth(0.65f)
                )
            }
            if(playlist.id != "favourite") {
                Spacer(modifier = Modifier.weight(0.5f))
                IconButton(onClick = {
                    vm.deletePlaylist(playlist)
                    vmController.goBack()
                    vm.makeToast("${playlist.name} deleted")
                }) {
                    Icon(imageVector = Icons.Filled.Delete, contentDescription = null)
                }
            }
            else{
                Spacer(modifier = Modifier.weight(1f))
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(1),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(8.dp),
            modifier = Modifier.fillMaxWidth()
        ){
            items(tracks) {track ->
                TrackRow(track!!, spotifyService::playTrack) {
                    scope.launch {
                        Log.i("PlaylistScreen", "Remove from playlist $playlist $track")
                        if(playlist.id != "favourite"){
                            vm.removeTrackFromPlaylist(track, playlist)
                            Log.i("PlaylistScreen", "Remove from playlist $playlist $track")
                        }
                        else {
                            vm.removeFromFavouriteTracks(track)
                        }
                        Log.i("PlaylistScreen", "Duration playlist ${playlist.durationInMs}")
                        playlist.durationInMs -= track.length
                        Log.i("PlaylistScreen", "Duration playlist ${playlist.durationInMs}")
                        durationText = millisecondsToHoursAndMinutes(playlist.durationInMs)
                        tracks = tracks.minus(track)
                        vm.makeToast("${track.name} deleted from ${playlist.name}")
                    }
                }
            }
        }
    }
}

@Composable
fun TrackRow(
    track: Track,
    onClick: KSuspendFunction1<PlayableUri, Unit>,
    onDelete: () -> Unit
) {
    val scope = rememberCoroutineScope()
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

@Composable
fun ChoosePlaylistScreen(uri: String){
//    val uiState by vm.searchUiState.collectAsState()
    val scope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current
    val vmController: NavControllerViewModel by inject()
    val vm: ProfileViewModel by inject()
    val uiState by vm.profileUiState.collectAsState()
    var playlists: List<Playlist> = listOf()
    val addToSelectedPlaylists: (Playlist) -> Unit = { playlists = playlists.plus(it)}
    val removeFromSelectedPlaylists: (Playlist) -> Unit = { playlists = playlists.minus(it)}
//    vm.getPlaylists()
    Column {
        Row {
            Text(
                text = "Choose playlist",
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                modifier = Modifier
                    .padding(0.dp, 10.dp)
                    .fillMaxWidth()
            )
        }
        PlaylistList(uiState, addToSelectedPlaylists, removeFromSelectedPlaylists)
        Row(modifier = Modifier.fillMaxWidth()) {
            Button(
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp),
                onClick = {
                    vmController.goToAddPlaylist()
                },
//                colors = ButtonDefaults.buttonColors(
//                    containerColor = Color.Gray,
//                    contentColor = Color.White
//                )
            ) {
                Text(text = "Create")
            }
            Button(
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp),
                onClick = {
                    playlists.forEach {
                        vm.addTrackToPlaylist(uri, it)
//                        vm.getPlaylists()
                    }
                    vmController.goBack()
                    val message = "Track added to ${playlists.joinToString(", ") { it.name }}"
                    vm.makeToast(message)
                },
            ) {
                Text(text = "Add")
            }
        }
    }
//    Box(contentAlignment = Alignment.BottomCenter){
//    }
}

@Composable
fun PlaylistList(
    uiState: ProfileUiState,
    addToSelectedPlaylists: (Playlist) -> Unit,
    removeFromSelectedPlaylists: (Playlist) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(1),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(570.dp)
    ){
        items(uiState.playlists) {
            PlaylistRow(it, addToSelectedPlaylists, removeFromSelectedPlaylists)
        }
    }
}

@Composable
fun PlaylistRow(
    playlist: Playlist,
    addToSelectedPlaylists: (Playlist) -> Unit,
    removeFromSelectedPlaylists: (Playlist) -> Unit
){
    val background = MaterialTheme.colorScheme.background
    val colorSelected = MaterialTheme.colorScheme.primaryContainer
    var color by remember { mutableStateOf(background) }
    var selected by remember { mutableStateOf(false) }
    Row(verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxSize()
            .background(color)
            .clickable {
                color = if (selected) {
                    removeFromSelectedPlaylists(playlist)
                    background
                } else {
                    addToSelectedPlaylists(playlist)
                    colorSelected

                }
                selected = !selected
            }) {
        GlideImage(
            imageModel =
            playlist.image
                ?: "https://sun9-25.userapi.com/impg/Z3epnPuW1AG9bY8vNk6CxvPUfDC8Glje-nfRVA/tHFcX2ef9rk.jpg?size=900x900&quality=96&sign=27b00a943c3ac22fbaa34b00db97bea8&c_uniq_tag=DeuKuphk22jYBIyArxc3iAF8-bHFXuRzK_HtgZbSCrM&type=album",
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .height(65.dp)
                .width(65.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.CenterStart) {
            Text(
                text = playlist.name,
                textAlign = TextAlign.Center,
                fontSize = 28.sp,
//                modifier = Modifier.padding(bottom = 4.dp)
            )
        }
    }
}

@Composable
fun AddPlaylistScreen(){
    var name by remember { mutableStateOf("") }
    val vmController: NavControllerViewModel by inject()
    val vm: ProfileViewModel by inject()
//    val vm: ProfileViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val keyboardController = LocalSoftwareKeyboardController.current
    val trailingIcon = @Composable {
        IconButton(
            onClick = {
                name = ""
            }) {
            Icon(imageVector = Icons.Default.Close, contentDescription = "")
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(0.8f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Enter the name of the playlist",
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                modifier = Modifier
                    .padding(0.dp, 20.dp)
                //                .fillMaxWidth()
            )
            TextField(
                value = name,
                onValueChange = { name = it },
                singleLine = true,
//                keyboardActions = KeyboardActions(
//                    onSearch = {
//                        keyboardController?.hide()
//                    }
//                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                leadingIcon = {
                    Icon(imageVector = Icons.Default.List, contentDescription = "")
                },
                trailingIcon = if (name.isEmpty()) null else trailingIcon,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 15.dp)
            )
            Row(modifier = Modifier.fillMaxWidth()) {
                Button(
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp),
                    onClick = {
                        vmController.goBack()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Gray,
                        contentColor = Color.White
                    )
                ) {
                    Text(text = "Cancel")
                }
                Button(
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp),
                    onClick = {
                        keyboardController?.hide()
                        vmController.goBack()
                        vm.addPlaylist(name)
                        vm.makeToast("$name added")
                    },
                ) {
                    Text(text = "Add")
                }
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