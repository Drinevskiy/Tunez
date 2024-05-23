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
import com.adamratzman.spotify.SpotifyException
import com.adamratzman.spotify.models.ContextUri
import com.adamratzman.spotify.models.PlayableUri
import com.example.tunez.R
import com.example.tunez.activities.ActionHomeActivity
import com.example.tunez.activities.BaseActivity
import com.example.tunez.activities.MainActivity
import com.example.tunez.auth.guardValidSpotifyApi
import com.example.tunez.ui.service.SpotifyService
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.supervisorScope

@Composable
fun ReleasesScreen(spotifyService: SpotifyService, activity: BaseActivity, modifier: Modifier = Modifier) {
    var searchResult: List<com.adamratzman.spotify.models.Track?>? by remember { mutableStateOf(listOf()) }
    val scope = rememberCoroutineScope()
    Column {
        Button(onClick = {
            scope.launch {
                searchResult = spotifyService.getNewReleases()
            }
        }) {
            Text(text = "Рекомендации")
        }
        LazyVerticalGrid(
            columns = GridCells.Fixed(1),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(8.dp),
            modifier = Modifier.fillMaxWidth()
        ){
            if(searchResult != null) {
                items(searchResult!!) { item ->
                    Button(
                        onClick = {
//                            runBlocking{
                                scope.launch {
                                    item?.uri?.let {
                                        spotifyService.play(it) {
                                        }
                                    }
                                    spotifyService.getCurrentTrack { n, a ->
                                    }
                                }
//                            }
                        },
                        modifier = Modifier
                            .height(42.dp)) {
                        Text("${item?.artists?.get(0)?.name} - ${item?.name}", fontSize = 14.sp, modifier = Modifier
                            .padding(5.dp)
                            .fillMaxWidth())
                    }
                }
            }
        }
    }
}