package com.example.tunez.screens

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tunez.R
import com.example.tunez.activities.BaseActivity
import com.example.tunez.activities.LoginActivity
import com.example.tunez.activities.user
import com.example.tunez.content.Track
import com.example.tunez.viewmodels.NavControllerViewModel
import com.example.tunez.viewmodels.ProfileUiState
import com.example.tunez.viewmodels.ProfileViewModel
import com.example.tunez.viewmodels.UserInfo
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.skydoves.landscapist.glide.GlideImage
import org.koin.androidx.compose.inject

@Composable
fun ProfileScreen(activity: BaseActivity, modifier: Modifier = Modifier, ){
    val vmNav: NavControllerViewModel by inject()
    var shouldLaunchLoginActivity by remember { mutableStateOf(user == null) }
    if (shouldLaunchLoginActivity) {
        LaunchedEffect(shouldLaunchLoginActivity) {
            activity.startActivity(Intent(activity, LoginActivity::class.java))
        }
    } else {
        val vm: ProfileViewModel by inject()
        val uiState by vm.profileUiState.collectAsState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Row(modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center) {
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "Profile",
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    modifier = Modifier
                        .padding(0.dp, 20.dp)
                )
                Spacer(modifier = Modifier.weight(0.7f))
                IconButton(
                    onClick = {
                        Firebase.auth.signOut()
                        user = null
                        vmNav.goToHome()

                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.ExitToApp,
                        contentDescription = null,
                        modifier = Modifier.size(35.dp, 35.dp)
                    )
                }
            }
            Column(
                modifier = Modifier
                    .padding(20.dp, 0.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Username: ${uiState.user.username}",
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray,
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)

                )
                Text(
                    text = "Email: ${uiState.user.email}",
                    style = TextStyle(
                        fontSize = 16.sp,
                        color = Color.Gray,
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)

                )
                Text(
                    text = "Status: ${uiState.user.role}",
                    style = TextStyle(
                        fontSize = 16.sp,
                        color = Color.Gray,
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)

                )
                Text(
                    text = "Favourite genres: ${uiState.user.genres.joinToString(", ")}",
                    style = TextStyle(
                        fontSize = 16.sp,
                        color = Color.Gray,
                    ),
                    modifier = Modifier.padding(bottom = 20.dp)
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
                if (uiState.user.favouritePlaylist.tracks.isNotEmpty()) {
                    FavouritePlaylist(uiState, vmNav)
                }
                else{
                    Text(
                        text = "You don't have any favourite tracks",
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
                PlaylistGrid(uiState, vmNav)
                if(uiState.user.role == "admin") {
                    AllUsersList(uiState, vmNav)
                }
                if(uiState.user.role == "artist"){
                    ArtistPanel(uiState, vmNav, vmNav::goToArtistEditTrack)
                }
            }
//        vm.getAllUsers()
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AllUsersList(uiState: ProfileUiState, vmController: NavControllerViewModel) {
    Text(
        text = "All users",
        textAlign = TextAlign.Center,
        fontWeight = FontWeight.Bold,
        fontSize = 19.sp,
        modifier = Modifier
            .padding(top = 10.dp, bottom = 20.dp)
            .fillMaxWidth()
    )
    FlowRow(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        maxItemsInEachRow = 1,
        modifier = Modifier.padding(bottom = 20.dp)
    ){
        uiState.allUsers.forEach {
            UserRow(it, vmController::goToUserProfile)
        }
    }
}

@Composable
fun UserRow(
    user: UserInfo,
    onClick: (UserInfo) -> Unit,
){
    Row(modifier = Modifier
        .fillMaxWidth()
        .clickable { onClick.invoke(user) }) {
        Row(modifier = Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                Text(
                    text = user.username!!,
                    fontSize = 22.sp,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = user.email!!,
                    fontSize = 17.sp,
                    modifier = Modifier
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = user.role!!,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ArtistPanel(uiState: ProfileUiState, vmController: NavControllerViewModel, onClick: (Track) -> Unit, isArtist: Boolean = true){
    Text(
        text = "Artist panel",
        textAlign = TextAlign.Center,
        fontWeight = FontWeight.Bold,
        fontSize = 19.sp,
        modifier = Modifier
            .padding(top = 10.dp, bottom = 20.dp)
            .fillMaxWidth()
    )

    FlowRow(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        maxItemsInEachRow = 2,
        modifier = Modifier.padding(bottom = 20.dp)
    ){
        uiState.artistTracks.forEach {
            ArtistTrackRow(it, onClick)
        }
        if(isArtist) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.47f)
                    .height(40.dp),
//            .background(MaterialTheme.colorScheme.tertiaryContainer, shape = RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.plus),
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier
                        .fillMaxWidth(0.2f)
                        .padding(0.dp, 5.dp)
                        .clickable {
                            vmController.goToAddTrack()
                        },
                )
            }
        }
    }
}

@Composable
fun ArtistTrackRow(track: Track, onClick: (Track) -> Unit){
    val background = MaterialTheme.colorScheme.background
    val colorBlocked = MaterialTheme.colorScheme.tertiaryContainer
    Log.i("ArtistTrackRow", track.toString())
    // Чтобы color успевало инициализироваться до использования
    var color by remember { mutableStateOf(background) }
    LaunchedEffect(track.blocked) {
        color = if (track.blocked) colorBlocked else background
    }
    Log.i("ArtistTrackRow", "$track $color")

    Row(modifier = Modifier
        .fillMaxWidth(0.47f)
        .background(color, shape = RoundedCornerShape(10.dp))
        .clickable { onClick.invoke(track) },
        horizontalArrangement = Arrangement.Center) {
        Text(
            text = "${track.name}",
            fontSize = 24.sp,
            modifier = Modifier.padding(0.dp, 5.dp),
            textAlign = TextAlign.Center
        )
    }
}


@Composable
fun FavouritePlaylist(uiState: ProfileUiState, vmController: NavControllerViewModel){
    Box(contentAlignment = Alignment.TopCenter,
        modifier = Modifier.fillMaxSize()) {
        Log.i("ProfileViewModel", uiState.user.favouritePlaylist.image.toString())
        GlideImage(
            imageModel = uiState.user.favouritePlaylist.image
                ?: "https://sun9-25.userapi.com/impg/Z3epnPuW1AG9bY8vNk6CxvPUfDC8Glje-nfRVA/tHFcX2ef9rk.jpg?size=900x900&quality=96&sign=27b00a943c3ac22fbaa34b00db97bea8&c_uniq_tag=DeuKuphk22jYBIyArxc3iAF8-bHFXuRzK_HtgZbSCrM&type=album",
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .clip(RoundedCornerShape(8.dp))
                .border(2.dp, Color.Black, RoundedCornerShape(8.dp))
                .clickable {
                    vmController.goToPlaylist(uiState.user.favouritePlaylist)
                },
        )
    }
}
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PlaylistGrid(uiState: ProfileUiState, vmController: NavControllerViewModel){
    FlowRow(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        maxItemsInEachRow = 2,
        modifier = Modifier.padding(bottom = 20.dp)
    ){
        Log.i("ProfileScreen", uiState.user.playlists.toString())
        uiState.user.playlists.forEach {
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
                        vmController.goToPlaylist(it)
//                        }
                    },
                )

        }
        Box(modifier = Modifier
            .fillMaxWidth(0.47f)
            .height(200.dp),
            contentAlignment = Alignment.Center
        ){
            Image(
                painter = painterResource(id = R.drawable.plus),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(120.dp)
                    .clickable {
                        vmController.goToAddPlaylist()
                    },
            )
        }
    }
}

@Preview
@Composable
fun ArtistTrackRowPreview(){
    ArtistTrackRow(track = Track(), onClick = {})
}

