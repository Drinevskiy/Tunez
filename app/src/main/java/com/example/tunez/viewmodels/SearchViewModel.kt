package com.example.tunez.viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.adamratzman.spotify.models.PlayableUri
import com.adamratzman.spotify.models.SpotifyImage
import com.adamratzman.spotify.models.Track
import com.example.tunez.activities.user
import com.example.tunez.ui.service.SpotifyService
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.database
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.lang.NullPointerException

class SearchViewModel(val spotifyService: SpotifyService): ViewModel() {
    private var _uiState = MutableStateFlow(SearchUiState())
    val searchUiState: StateFlow<SearchUiState> = _uiState.asStateFlow()
    fun updateUiState(uiState: SearchUiState){
        _uiState.update {
            it.copy(
                searchResult = uiState.searchResult,
                query = uiState.query
            )
        }
    }
    fun updateQuery(query: String){
        _uiState.update {
            it.copy(
                query = query
            )
        }
    }
    fun clear(){
        updateQuery("")
    }
    fun search(){
        viewModelScope.launch {
            val query = searchUiState.value.query
            if(query.isNotEmpty()){
                val searchResult = spotifyService.getTracks(query)
                updateUiState(searchUiState.value.copy(
                    searchResult = searchResult,
                    query = query
                ))
            }
        }
    }
    fun play(uri: PlayableUri){
        viewModelScope.launch {
            spotifyService.play(uri)
        }
    }
}

data class SearchUiState(
    var searchResult: List<com.adamratzman.spotify.models.Track>? = listOf(),
    var query: String = ""
)