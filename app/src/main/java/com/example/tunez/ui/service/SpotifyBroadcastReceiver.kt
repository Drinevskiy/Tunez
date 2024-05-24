package com.example.tunez.ui.service

import com.adamratzman.spotify.notifications.AbstractSpotifyBroadcastReceiver
import com.adamratzman.spotify.notifications.SpotifyMetadataChangedData
import com.adamratzman.spotify.notifications.SpotifyPlaybackStateChangedData
import com.example.tunez.viewmodels.HomeViewModel

class SpotifyBroadcastReceiver(private val vm: HomeViewModel): AbstractSpotifyBroadcastReceiver() {
    override fun onMetadataChanged(data: SpotifyMetadataChangedData) {
        vm.handleMetadata(data)
    }

    override fun onPlaybackStateChanged(data: SpotifyPlaybackStateChangedData) {
        vm.handlePlaybackState(data)
    }
}