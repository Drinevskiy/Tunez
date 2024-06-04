package com.example.tunez.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.adamratzman.spotify.models.Track
import com.example.tunez.activities.user
import com.example.tunez.viewmodels.NavControllerViewModel
import com.example.tunez.viewmodels.ProfileViewModel
import com.example.tunez.viewmodels.UserInfo
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.coroutines.launch
import org.koin.androidx.compose.inject

@Composable
fun ArtistProfileScreen(userInfo: UserInfo){
    val vmController: NavControllerViewModel by inject()
    val vm: ProfileViewModel by inject()
    val uiState by vm.profileUiState.collectAsState()
    LaunchedEffect(Unit) {
        vm.getCurrentArtistTracks(userInfo.uid!!)
    }
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
                    text = "${userInfo.username}",
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    modifier = Modifier
                        .padding(0.dp, 10.dp)
                        .fillMaxWidth(0.65f)
                )
            }
            Spacer(modifier = Modifier.weight(1f))
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(1),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(8.dp),
            modifier = Modifier.fillMaxWidth()
        ){
            items(uiState.currentArtistTracks) {track ->
                if(!track.blocked) {
                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .clickable { vm.playArtistTrack(track) }) {
                        GlideImage(
                            imageModel =
                            "https://sun9-25.userapi.com/impg/Z3epnPuW1AG9bY8vNk6CxvPUfDC8Glje-nfRVA/tHFcX2ef9rk.jpg?size=900x900&quality=96&sign=27b00a943c3ac22fbaa34b00db97bea8&c_uniq_tag=DeuKuphk22jYBIyArxc3iAF8-bHFXuRzK_HtgZbSCrM&type=album",
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
                                    text = "${track.name}",
                                    fontSize = 22.sp,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                                Text(
                                    text = "${userInfo.username}",
                                    fontSize = 17.sp,
                                    modifier = Modifier
                                )
                            }
                        }
                    }
//                    Row(modifier = Modifier
//                        .fillMaxWidth()
//                        .background(color = MaterialTheme.colorScheme.inversePrimary, shape = MaterialTheme.shapes.small)
//                        .clickable { vm.playArtistTrack(track)},
//                        horizontalArrangement = Arrangement.Center
//                    ) {
//                        Text(text = "${track.name}",
//                            textAlign = TextAlign.Center,
//                            fontSize = 22.sp,
//                            modifier = Modifier.padding(0.dp, 5.dp)
//                        )
//                    }
                }
            }
        }
    }
}