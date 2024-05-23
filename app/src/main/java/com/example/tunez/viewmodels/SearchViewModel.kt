package com.example.tunez.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tunez.ui.service.SpotifyService
import kotlinx.coroutines.launch

class SearchViewModel(val spotifyService: SpotifyService): ViewModel() {
    var searchResult: List<com.adamratzman.spotify.models.Track>? by mutableStateOf(listOf())
    var query by mutableStateOf("")
    fun search(){
        viewModelScope.launch {
            searchResult = spotifyService.getTracks(query)
        }
    }
}