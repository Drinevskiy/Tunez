package com.example.tunez.screens

import android.content.Intent
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.tunez.activities.BaseActivity
import com.example.tunez.activities.LoginActivity
import com.example.tunez.activities.NavBarItems
import com.example.tunez.activities.PlaylistActivity
import com.example.tunez.activities.Routes
import com.example.tunez.activities.user
import com.example.tunez.content.Playlist
import com.example.tunez.viewmodels.AppViewModelProvider
import com.example.tunez.viewmodels.NavControllerViewModel
import com.example.tunez.viewmodels.ProfileUiState
import com.example.tunez.viewmodels.ProfileViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.gson.Gson
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.androidx.compose.inject


@Composable
fun FavouritePlaylist(uiState: ProfileUiState, vm: ProfileViewModel, activity: BaseActivity,  navController: NavController, scope: CoroutineScope){
//    scope.launch {
//        vm.loadFavouriteImage()
//    }
    val vmNav: NavControllerViewModel by inject()
    Box(contentAlignment = Alignment.TopCenter,
        modifier = Modifier.fillMaxSize()) {
        Log.i("ProfileViewModel", uiState.favouritePlaylist.image.toString())
        GlideImage(
            imageModel = uiState.favouritePlaylist.image
                ?: "https://sun9-25.userapi.com/impg/Z3epnPuW1AG9bY8vNk6CxvPUfDC8Glje-nfRVA/tHFcX2ef9rk.jpg?size=900x900&quality=96&sign=27b00a943c3ac22fbaa34b00db97bea8&c_uniq_tag=DeuKuphk22jYBIyArxc3iAF8-bHFXuRzK_HtgZbSCrM&type=album",
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .clip(RoundedCornerShape(8.dp))
                .border(2.dp, Color.Black, RoundedCornerShape(8.dp))
                .clickable {
                    vmNav.goToPlaylist(uiState.favouritePlaylist)
//                    val intent = Intent(activity, PlaylistActivity::class.java)
//                    intent.putStringArrayListExtra("tracksUri", ArrayList(uiState.favouritePlaylist.tracks))
//                    intent.putExtra("playlistSerialized", Gson().toJson(uiState.favouritePlaylist))
//                    activity.startActivity(intent)
                },
        )
    }
}
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PlaylistGrid(uiState: ProfileUiState){
    FlowRow(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        maxItemsInEachRow = 2,
        modifier = Modifier.padding(bottom = 20.dp)
    ){
        if(uiState.favouritePlaylist.tracks != null) {
            uiState.favouritePlaylist.tracks.forEach {
                GlideImage(
                    imageModel =

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

                        },
                    )

            }
        }
    }
}

@Composable
fun ProfileScreen(activity: BaseActivity, navController: NavController, modifier: Modifier = Modifier, vm: ProfileViewModel = viewModel(factory = AppViewModelProvider.Factory)) {
    val uiState by vm.profileUiState.collectAsState()
    val scope = rememberCoroutineScope()
    var shouldLaunchLoginActivity by remember { mutableStateOf(user == null) }

    if (shouldLaunchLoginActivity) {
        LaunchedEffect(shouldLaunchLoginActivity) {
            activity.startActivity(Intent(activity, LoginActivity::class.java))
        }
    } else {
        vm.getAllInfo()
        vm.getFavouriteTracks()
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
                        navController.navigate(Routes.Home.route) {
                            popUpTo(navController.graph.findStartDestination().id)
                        }
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
                        text = "Username: ${uiState.username}",
                        style = TextStyle(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray,
                        ),
                        modifier = Modifier.padding(bottom = 8.dp)

                    )
                    Text(
                        text = "Email: ${uiState.email}",
                        style = TextStyle(
                            fontSize = 16.sp,
                            color = Color.Gray,
                        ),
                        modifier = Modifier.padding(bottom = 8.dp)

                    )
                    Text(
                        text = "Status: ${uiState.role}",
                        style = TextStyle(
                            fontSize = 16.sp,
                            color = Color.Gray,
                        ),
                        modifier = Modifier.padding(bottom = 8.dp)

                    )
                    Text(
                        text = "Favourite genres: ${uiState.genres.joinToString(", ")}",
                        style = TextStyle(
                            fontSize = 16.sp,
                            color = Color.Gray,
                        ),
                        modifier = Modifier.padding(bottom = 20.dp)
                    )
                    if (uiState.favouritePlaylist.tracks.isNotEmpty()) {
                        Text(
                            text = "Favourite tracks",
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold,
                            fontSize = 19.sp,
                            modifier = Modifier
                                .padding(bottom = 20.dp)
                                .fillMaxWidth()
                        )
//                        vm.loadFavouriteImage()
                        FavouritePlaylist(uiState, vm, activity, navController, scope)
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
//                    }
                    if (uiState.favouritePlaylist.tracks.isNotEmpty()) {
                        PlaylistGrid(uiState)
                    }
//                }

            }
//        vm.getAllUsers()
        }
    }
}




