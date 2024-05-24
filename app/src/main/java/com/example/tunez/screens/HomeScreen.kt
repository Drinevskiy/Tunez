package com.example.tunez.screens

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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
import com.example.tunez.viewmodels.HomeUiState
import com.example.tunez.viewmodels.HomeViewModel
import com.example.tunez.viewmodels.SearchViewModel
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@Composable
fun HomeScreen(spotifyService: SpotifyService, activity: BaseActivity, modifier: Modifier = Modifier, vm: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)) {
    val uiState by vm.homeUiState.collectAsState()
    val scope = rememberCoroutineScope()
    Column(modifier = Modifier.padding(20.dp)) {
        GlideImage(
            imageModel = uiState.image?.url,
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
                .clip(RoundedCornerShape(8.dp))
                .border(2.dp, Color.Black, RoundedCornerShape(8.dp)),
        )
        Text(
            text = uiState.name!!,
            fontSize = 28.sp,
            modifier = Modifier.padding(0.dp, 10.dp, 0.dp, 5.dp)
        )
        Text(
            text = uiState.authors?.joinToString(", ")!!,
            fontSize = 20.sp,
        )
        Spacer(modifier = Modifier.weight(1f))

        MusicProgressBar(vm, scope)
        Row(horizontalArrangement = Arrangement.SpaceAround) {
            IconButton(
                onClick = {
                   scope.launch{
                       vm.previous()
                   }
                }
            ) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Next")
            }
            Spacer(modifier = modifier.weight(1f))
            if(uiState.isPlaying) {
                IconButton(
                    onClick = {
                        scope.launch(){
                            vm.pause()
                        }
                    },
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_pause_24),
                        contentDescription = "Pause"
                    )
                }
            } else {
                IconButton(
                    onClick = {
                        scope.launch {
                            vm.resume()
                        }
                    },
                ) {
                    Icon(imageVector = Icons.Filled.PlayArrow, contentDescription = "Resume")
                }
            }
            Spacer(modifier = modifier.weight(1f))
            IconButton(
                onClick = {
                    scope.launch{
                        vm.next()
                    }
                }
            ) {
                Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "Next")
            }
        }
    }
}

@Composable
fun MusicProgressBar(
    vm: HomeViewModel,
    scope: CoroutineScope
) {
    val uiState by vm.homeUiState.collectAsState()
//    var isPlaying by remember { mutableStateOf(false) }
//    var currentPlaybackPosition by remember { mutableStateOf(uiState.position) }

//    LaunchedEffect(uiState.position, isPlaying) {
//        scope.launch {
//            if (isPlaying) {
//                currentPlaybackPosition = uiState.position
//            }
//        }
//    }
//    LaunchedEffect(uiState.position) {
//        currentPlaybackPosition = uiState.position
//    }
    Box(
        modifier = Modifier
            .fillMaxWidth(),
//            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column {
            Slider(
                value = uiState.position,
                onValueChange = {
//                    currentPlaybackPosition = it
                    vm.changePosition(it)
                },
//                onValueChangeFinished = {
//                    isPlaying = !isPlaying
//                },
//                modifier = Modifier.weight(1f),
                valueRange = 0f..uiState.trackLength,
                colors = SliderDefaults.colors(
                    thumbColor = Color.White,
                    activeTrackColor = Color.LightGray,
                    inactiveTrackColor = Color.Gray.copy(alpha = 0.5f)
                )
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
//                    text = formatDuration(currentPlaybackPosition),
                    text = formatDuration(uiState.position),
                    fontSize = 13.sp,// формат времени в минуты:секунды
                    modifier = Modifier.padding(start = 5.dp)
                    )
                Text(
                    text = formatDuration(uiState.trackLength),
                    fontSize = 13.sp,// общая продолжительность песни
                    modifier = Modifier.padding(end = 5.dp)
                )
            }
        }
    }
}

private fun formatDuration(seconds: Float): String {
    val totalSeconds = seconds.toInt()
    val minutes = totalSeconds / 60
    val remainingSeconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, remainingSeconds)
}