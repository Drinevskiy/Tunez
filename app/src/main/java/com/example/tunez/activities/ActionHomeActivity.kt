package com.example.tunez.activities

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.adamratzman.spotify.SpotifyException
import com.adamratzman.spotify.models.ContextUri
import com.adamratzman.spotify.models.PlayableUri
import com.example.tunez.R
import com.example.tunez.auth.guardValidSpotifyApi
import com.example.tunez.ui.service.SpotifyService
import com.example.tunez.ui.theme.TunezTheme
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class ActionHomeActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        guardValidSpotifyApi(ActionHomeActivity::class.java) { api ->
            if (!api.isTokenValid(true).isValid) throw SpotifyException.ReAuthenticationNeededException()

            setContent {
                TunezTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        Greeting(this)
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(activity: BaseActivity, modifier: Modifier = Modifier) {
    val spotifyService: SpotifyService = SpotifyService()
    var name: String? by remember{ mutableStateOf("") }
    var author: String? by remember{ mutableStateOf("") }
    var isPlaying by remember { mutableStateOf(false) }
    var searchResult: List<com.adamratzman.spotify.models.Track>? by remember { mutableStateOf(listOf()) }
    var query by remember { mutableStateOf("") }
    Column {
        Row {
            Text(text = author + " - " + name)
        }
        Row(horizontalArrangement = Arrangement.SpaceAround) {
            Button(
                onClick = {
                    runBlocking{
                        launch{
//                            spotifyService.play(PlayableUri.invoke("spotify:track:4Yf5bqU3NK4kNOypcrLYwU")) {
//                                isPlaying = it
//                            }
                            spotifyService.getCurrentTrack { n, a ->
                                name = n
                                author = a
                            }
                        }
                    }
                }
            ) {
                Text(text = "Linkin Park")
            }
            Spacer(modifier = modifier.weight(1f))
            Button(
                onClick = {
                    runBlocking{
                        launch{
//                            spotifyService.play(ContextUri.invoke("spotify:playlist:37i9dQZF1E39kHqVXB5cTm")) {
//                                isPlaying = it
//                            }
                            spotifyService.getCurrentTrack { n, a ->
                                name = n
                                author = a
                            }
                        }
                    }
                },
            ) {
                Text(text = "Плейлист")
            }
        }
        Row(horizontalArrangement = Arrangement.SpaceAround) {
            IconButton(
                onClick = {
                    runBlocking {
                        launch{
//                            spotifyService.previous {
////                                isPlaying = it
//                            }
                            spotifyService.getCurrentTrack { n, a ->
                                name = n
                                author = a
                            }
                        }
                    }
                }
            ) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Next")
            }
            Spacer(modifier = modifier.weight(1f))
            if(isPlaying) {
                IconButton(
                    onClick = {
                        runBlocking {
//                            spotifyService.pause {
//                                isPlaying = it
//                            }
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
                        runBlocking {
                            launch{
//                                spotifyService.resume {
////                                    isPlaying = it
//                                }
                                spotifyService.getCurrentTrack { n, a ->
                                    name = n
                                    author = a
                                }
                            }
                        }
                    },
                ) {
                    Icon(imageVector = Icons.Filled.PlayArrow, contentDescription = "Resume")
                }
            }
            Spacer(modifier = modifier.weight(1f))
            IconButton(
                onClick = {
                    runBlocking {
                        launch{
//                            spotifyService.next {
////                                isPlaying = it
//                            }
                            spotifyService.getCurrentTrack { n, a ->
                                name = n
                                author = a
                            }
                        }
                    }
                }
            ) {
                Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "Next")
            }
        }
        Row {
            IconButton(
                onClick = {
                    runBlocking {
//                        searchResult = spotifyService.getTracks(query)
                    }
                }) {
                Icon(imageVector = Icons.Default.Search, contentDescription = "")
            }
            Spacer(modifier = modifier.width(8.dp))
            TextField(value = query,
                onValueChange = {query = it},
                label = { Text("Поиск") })
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
                            runBlocking{
                                launch {
//                                    spotifyService.play(item.uri) {
//                                        isPlaying = it
//                                    }
                                    spotifyService.getCurrentTrack { n, a ->
                                        name = n
                                        author = a
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .height(42.dp)) {
                        Text("${item.artists.get(0).name} - ${item.name}", fontSize = 14.sp, modifier = Modifier
                            .padding(5.dp)
                            .fillMaxWidth())
                    }
                }
            }
        }
    }
}