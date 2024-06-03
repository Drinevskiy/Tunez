package com.example.tunez.screens

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
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
import com.example.tunez.viewmodels.SearchUiState
import com.example.tunez.viewmodels.SearchViewModel
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.androidx.compose.inject
import kotlin.reflect.KFunction1

@Composable
fun SearchScreen(modifier: Modifier = Modifier, vm: SearchViewModel = viewModel(factory = AppViewModelProvider.Factory)) {
    val uiState by vm.searchUiState.collectAsState()
    val scope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current
    val trailingIcon = @Composable {
            IconButton(
                onClick = {
                    scope.launch {
                        vm.clear()
                    }
                }) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "")
            }
    }
    Column {
        Row {
            Text(
                text = "Search",
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                modifier = Modifier
                    .padding(0.dp, 10.dp)
                    .fillMaxWidth()
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(10.dp)) {
            TextField(value = uiState.query,
                onValueChange = { vm.updateQuery(it) },
                singleLine = true,
                keyboardActions = KeyboardActions(
                    onSearch = {
                        vm.search()
                        keyboardController?.hide()
                    }
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "")
                },
                trailingIcon = if (uiState.query.isEmpty()) null else trailingIcon,
                modifier = Modifier.weight(1f)
            )
        }
        TracksList(uiState, vm)
    }
}

@Composable
fun TracksList(uiState: SearchUiState, vm: SearchViewModel){
    val vmNav: NavControllerViewModel by inject()
    val vmProfile: ProfileViewModel by inject()
    LazyVerticalGrid(
        columns = GridCells.Fixed(1),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(8.dp),
        modifier = Modifier.fillMaxWidth()
    ){
        if(uiState.searchResult != null) {
            items(uiState.searchResult!!) {
                TrackRow(it, vm::play, vmProfile::addToFavouriteTracks, vmNav::goToChoosePlaylist)
            }
        }
    }
}

@Composable
fun TrackRow(
    track: Track,
    onClick: (PlayableUri) -> Unit,
    addToFavourite: (Track) -> Unit,
    goToChoosePlaylist: (Track) -> Unit
) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .clickable { onClick.invoke(track.uri) }) {
        GlideImage(
            imageModel =
            track.album.images?.get(0)?.url ?:
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
                    text = track.name,
                    fontSize = 22.sp,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = track.artists.map { it.name }.joinToString(", "),
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
                    TrackOptionMenu(
                        track = track,
                        options = mapOf("Add to favourite" to addToFavourite, "Add to playlist" to goToChoosePlaylist),
//                            mapOf("Add to playlist" to goToChoosePlaylist),
//                            ),
//                        onOptionSelected = addToFavourite
                    )
                }
            }
        }
    }
}

@Composable
fun TrackOptionMenu(
    track: Track,
    options: Map<String, (Track) -> Unit>,
//    onOptionSelected: (Track) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .clickable { expanded = !expanded }
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Filled.MoreVert,
            contentDescription = "Track options"
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.width(135.dp)
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(text = option.key)
                    },
                    onClick = {
                        option.value.invoke(track)// as? Function<Unit>
//                        onOptionSelected.invoke(track)
                        expanded = false
                    }
                )
            }
        }
    }
}


