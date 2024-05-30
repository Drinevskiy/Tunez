package com.example.tunez.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tunez.viewmodels.AppViewModelProvider
import com.example.tunez.viewmodels.ReleasesUiState
import com.example.tunez.viewmodels.ReleasesViewModel
import kotlinx.coroutines.launch

@Composable
fun ReleasesScreen(modifier: Modifier = Modifier, vm: ReleasesViewModel = viewModel(factory = AppViewModelProvider.Factory)) {
    val uiState by vm.releasesUiState.collectAsState()
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxSize())
    {
        Row {
            Text(
                text = "Releases",
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                modifier = Modifier.padding(0.dp, 10.dp).fillMaxWidth()
            )
        }
        TracksList(uiState, vm)
        Button(modifier = Modifier.fillMaxWidth().padding(8.dp),
            onClick = {
                scope.launch {
                    vm.getReleases()
                }
            }
        ) {
            Text(text = "Update")
        }
    }
}

@Composable
fun TracksList(uiState: ReleasesUiState, vm: ReleasesViewModel){
    LazyVerticalGrid(
        columns = GridCells.Fixed(1),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(8.dp),
        modifier = Modifier.fillMaxWidth().height(570.dp)
    ){
        if(uiState.releases != null) {
            items(uiState.releases!!) {
                TrackRow(it, vm::play){vm.addToFavouriteTracks(it)}
            }
        }
    }
}
