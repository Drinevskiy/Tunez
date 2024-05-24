package com.example.tunez.screens

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.adamratzman.spotify.SpotifyException
import com.adamratzman.spotify.models.ContextUri
import com.adamratzman.spotify.models.PlayableUri
import com.example.tunez.R
import com.example.tunez.activities.ActionHomeActivity
import com.example.tunez.activities.BaseActivity
import com.example.tunez.activities.MainActivity
import com.example.tunez.auth.guardValidSpotifyApi
import com.example.tunez.ui.service.SpotifyService
import com.example.tunez.viewmodels.AppViewModelProvider
import com.example.tunez.viewmodels.SearchViewModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.supervisorScope

@Composable
fun SearchScreen(spotifyService: SpotifyService, activity: BaseActivity, modifier: Modifier = Modifier, vm: SearchViewModel = viewModel(factory = AppViewModelProvider.Factory)) {
//    activity.guardValidSpotifyApi(MainActivity::class.java) { api ->
//        if (!api.isTokenValid(true).isValid) throw SpotifyException.ReAuthenticationNeededException()
//    }
//    val spotifyService: SpotifyService = SpotifyService(activity)
    var searchResult: List<com.adamratzman.spotify.models.Track>? by remember { mutableStateOf(listOf()) }
    var query by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    Column {
        Row {
            IconButton(
                onClick = {
                    scope.launch {
                        vm.search()
//                        searchResult = spotifyService.getTracks(vm.query)
                    }
                }) {
                Icon(imageVector = Icons.Default.Search, contentDescription = "")
            }
            Spacer(modifier = modifier.width(8.dp))
            TextField(value = vm.query,
                onValueChange = { vm.query = it },
                label = { Text("Поиск") })
        }
        LazyVerticalGrid(
            columns = GridCells.Fixed(1),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(8.dp),
            modifier = Modifier.fillMaxWidth()
        ){
            if(vm.searchResult != null) {
                items(vm.searchResult!!) { item ->
                    Button(
                        onClick = {
                            runBlocking{
                                launch {
                                    spotifyService.play(item.uri) {
                                    }
                                    spotifyService.getCurrentTrack { n, a ->
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .height(42.dp)) {
                        Text("${item.artists.map { it.name }.joinToString(", ")} - ${item.name}", fontSize = 14.sp, modifier = Modifier
                            .padding(5.dp)
                            .fillMaxWidth())
                    }
                }
            }
        }
    }
}