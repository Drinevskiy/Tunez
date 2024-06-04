package com.example.tunez.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.adamratzman.spotify.models.PlayableUri
import com.adamratzman.spotify.models.Track
import com.example.tunez.activities.user
import com.example.tunez.viewmodels.AppViewModelProvider
import com.example.tunez.viewmodels.NavControllerViewModel
import com.example.tunez.viewmodels.ProfileViewModel
import com.example.tunez.viewmodels.ReleasesUiState
import com.example.tunez.viewmodels.ReleasesViewModel
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.coroutines.launch
import org.koin.androidx.compose.inject

@Composable
fun ReleasesScreen(modifier: Modifier = Modifier, vm: ReleasesViewModel = viewModel(factory = AppViewModelProvider.Factory)) {
    val uiState by vm.releasesUiState.collectAsState()

    Column(modifier = Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState()))
    {
        Text(
            text = "Charts",
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            modifier = Modifier
                .padding(0.dp, 10.dp)
                .fillMaxWidth()
        )
        ChartList(uiState, vm)
        Text(
            text = "Releases",
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            modifier = Modifier
                .padding(0.dp, 10.dp)
                .fillMaxWidth()
        )
//        ChartList(uiState, vm)
        TracksList(uiState, vm)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChartList(uiState: ReleasesUiState, vm: ReleasesViewModel){
    val vmNav: NavControllerViewModel by inject()
    val vmProfile: ProfileViewModel by inject()
    FlowRow(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        maxItemsInEachRow = 1,
        modifier = Modifier.padding(start = 8.dp, top = 8.dp, end = 8.dp, bottom = 10.dp)
    ){
        uiState.charts.take(15).forEach {
            TrackRow(it!!, vm::play, vmProfile::addToFavouriteTracks, vmNav::goToChoosePlaylist, vmProfile::addToEndOfQueue)
        }
    }
//    LazyVerticalGrid(
//        columns = GridCells.Fixed(1),
//        verticalArrangement = Arrangement.spacedBy(16.dp),
//        contentPadding = PaddingValues(8.dp),
//        modifier = Modifier
//            .fillMaxWidth()
////            .height(570.dp)
//    ){
////        items(uiState.charts) {
////            TrackRow(it, vm::play, vmProfile::addToFavouriteTracks, vmNav::goToChoosePlaylist)
////        }
//        items(uiState.chartsString) {
//
//        }
//    }
}

//Временный пока не работает api. Заменить на TrackRow
@Composable
fun ChartRow(
    uri: String,
    onClick: (PlayableUri) -> Unit,
    addToFavourite: (Track) -> Unit,
    goToChoosePlaylist: (Track) -> Unit
) {
    Row(modifier = Modifier
        .fillMaxWidth()
//        .clickable { onClick.invoke(track.uri) }) {
    ){
        GlideImage(
            imageModel =
//            track.album.images?.get(0)?.url ?:
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
                    text = uri,
                    fontSize = 22.sp,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = "Artist",
                    fontSize = 17.sp,
                    modifier = Modifier
                )
            }
            if (user != null) {
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
//                    TrackOptionMenu(
//                        track = track,
//                        options = mapOf("Add to favourite" to addToFavourite, "Add to playlist" to goToChoosePlaylist),
////                            mapOf("Add to playlist" to goToChoosePlaylist),
////                            ),
////                        onOptionSelected = addToFavourite
//                    )
                }
            }
        }
    }
}
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TracksList(uiState: ReleasesUiState, vm: ReleasesViewModel){
    val vmNav: NavControllerViewModel by inject()
    val vmProfile: ProfileViewModel by inject()
    FlowRow(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        maxItemsInEachRow = 1,
        modifier = Modifier.padding(start = 8.dp, top = 8.dp, end = 8.dp, bottom = 10.dp)
    ){
        uiState.releases.forEach {
            TrackRow(it, vm::play, vmProfile::addToFavouriteTracks, vmNav::goToChoosePlaylist, vmProfile::addToEndOfQueue)
        }
    }
}
