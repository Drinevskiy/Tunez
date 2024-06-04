package com.example.tunez.screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.adamratzman.spotify.models.PlayableUri
import com.adamratzman.spotify.models.Track
import com.example.tunez.R
import com.example.tunez.content.Playlist
import com.example.tunez.roles.IAccount
import com.example.tunez.viewmodels.NavControllerViewModel
import com.example.tunez.viewmodels.ProfileViewModel
import com.example.tunez.viewmodels.UserInfo
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.inject
import kotlin.reflect.KFunction1

@Composable
fun ProfileInfoScreen(userInfo: UserInfo, modifier: Modifier = Modifier, ){
    val vmController: NavControllerViewModel by inject()
    val vm: ProfileViewModel by inject()
    val uiState by vm.profileUiState.collectAsState()
    LaunchedEffect(uiState.artistTracks) {
//        delay(1000)
        vm.getInfoAboutUser(userInfo)
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
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
            Spacer(modifier = Modifier.weight(0.7f))
            Text(
                text = "${uiState.currentUserForAdmin.username}",
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                modifier = Modifier
                    .padding(0.dp, 20.dp)
            )
            Spacer(modifier = Modifier.weight(1f))
        }
        Column(
            modifier = Modifier
                .padding(20.dp, 0.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = "Email: ${uiState.currentUserForAdmin.email}",
                style = TextStyle(
                    fontSize = 16.sp,
                    color = Color.Gray,
                ),
                modifier = Modifier.padding(bottom = 8.dp)

            )
            Text(
                text = "Status: ${uiState.currentUserForAdmin.role}",
                style = TextStyle(
                    fontSize = 16.sp,
                    color = Color.Gray,
                ),
                modifier = Modifier.padding(bottom = 8.dp)

            )
            Text(
                text = "Favourite tracks",
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontSize = 19.sp,
                modifier = Modifier
                    .padding(bottom = 20.dp)
                    .fillMaxWidth()
            )
            if (uiState.currentUserForAdmin.favouritePlaylist.tracks.isNotEmpty()) {
                UserFavouritePlaylist(uiState.currentUserForAdmin.favouritePlaylist, vmController)
            }
            else{
                Text(
                    text = "User don't have any favourite tracks",
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .padding(top = 10.dp, bottom = 20.dp)
                        .fillMaxWidth()
                )
            }
            Text(
                text = "Playlists",
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontSize = 19.sp,
                modifier = Modifier
                    .padding(top = 10.dp, bottom = 20.dp)
                    .fillMaxWidth()
            )
            if(uiState.currentUserForAdmin.playlists.isNotEmpty()){
                UserPlaylistGrid(uiState.currentUserForAdmin.playlists, vmController)
            }
            else{
                Text(
                    text = "User don't have any playlists",
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .padding(top = 10.dp, bottom = 20.dp)
                        .fillMaxWidth()
                )
            }
            if(userInfo.role == "artist") {
//                vm.getArtistTracks(userInfo.uid!!)
                if (uiState.artistTracks.isNotEmpty()) {
                    ArtistPanel(uiState, vmController, vmController::goToArtistEditTrackForAdmin, false)
                }
            }
        }

    }
}

@Composable
fun UserFavouritePlaylist(playlist: Playlist, vmController: NavControllerViewModel){
    Box(contentAlignment = Alignment.TopCenter,
        modifier = Modifier.fillMaxSize()) {
        Log.i("ProfileViewModel", playlist.image.toString())
        GlideImage(
            imageModel = playlist.image
                ?: "https://sun9-25.userapi.com/impg/Z3epnPuW1AG9bY8vNk6CxvPUfDC8Glje-nfRVA/tHFcX2ef9rk.jpg?size=900x900&quality=96&sign=27b00a943c3ac22fbaa34b00db97bea8&c_uniq_tag=DeuKuphk22jYBIyArxc3iAF8-bHFXuRzK_HtgZbSCrM&type=album",
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .clip(RoundedCornerShape(8.dp))
                .border(2.dp, Color.Black, RoundedCornerShape(8.dp))
                .clickable {
                    vmController.goToUserPlaylist(playlist)
                },
        )
    }
}
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun UserPlaylistGrid(playlists: List<Playlist>, vmController: NavControllerViewModel){
    FlowRow(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        maxItemsInEachRow = 2,
        modifier = Modifier.padding(bottom = 20.dp)
    ){
        Log.i("ProfileScreen", playlists.toString())
        playlists.forEach {
            GlideImage(
                imageModel = it.image ?:
//                    it.album.images?.get(0)?.url ?:
                "https://sun9-25.userapi.com/impg/Z3epnPuW1AG9bY8vNk6CxvPUfDC8Glje-nfRVA/tHFcX2ef9rk.jpg?size=900x900&quality=96&sign=27b00a943c3ac22fbaa34b00db97bea8&c_uniq_tag=DeuKuphk22jYBIyArxc3iAF8-bHFXuRzK_HtgZbSCrM&type=album",
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .fillMaxWidth(0.47f)
                    .height(200.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .border(2.dp, Color.Black, RoundedCornerShape(8.dp))
                    .clickable {
//                        if(it.image != null){
                        vmController.goToUserPlaylist(it)
//                        }
                    },
            )

        }
    }
}

@Composable
fun UserPlaylistScreen(playlist: Playlist) {
    val scope = rememberCoroutineScope()
    var tracks: List<Track?> by remember { mutableStateOf(playlist.tracks) }
    var durationText by remember { mutableStateOf(millisecondsToHoursAndMinutes (playlist.durationInMs)) }
    val vmController: NavControllerViewModel by inject()
    val vm: ProfileViewModel by inject()
    val uiState by vm.profileUiState.collectAsState()
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
                    vm.deleteUserPlaylist(uiState.currentUserForAdmin, playlist)
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
                TrackRow(track!!, vm::play) {
                    scope.launch {
                        Log.i("PlaylistScreen", "Remove from playlist ${playlist.id} $track")
                        if(playlist.id != "favourite"){
                            vm.deleteTrackFromUserPlaylist(uiState.currentUserForAdmin, track, playlist)
//                            Log.i("PlaylistScreen", "Remove from playlist $playlist $track")
                        }
                        else {
                            vm.deleteTrackFromUserFavouriteTracks(uiState.currentUserForAdmin, track)
                        }
//                        Log.i("PlaylistScreen", "Duration playlist ${playlist.durationInMs}")
                        playlist.durationInMs -= track.length
//                        Log.i("PlaylistScreen", "Duration playlist ${playlist.durationInMs}")
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
fun TrackRowPlaylist(
    track: Track,
    onClick: KFunction1<PlayableUri, Unit>,
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


