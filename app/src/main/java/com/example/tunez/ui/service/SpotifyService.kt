package com.example.tunez.ui.service

import android.util.Log
import com.adamratzman.spotify.models.ContextUri
import com.adamratzman.spotify.models.Device
import com.adamratzman.spotify.models.PlayableUri
import com.adamratzman.spotify.models.Track
import com.example.tunez.SpotifyPlaygroundApplication
import com.example.tunez.activities.BaseActivity
import com.example.tunez.activities.MainActivity
import com.example.tunez.auth.guardValidSpotifyApi
import com.example.tunez.data.Constants
import com.example.tunez.utils.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class SpotifyService() {
    lateinit var baseActivity: BaseActivity
    // Play Playlist
    suspend fun play(playlistUri: ContextUri) {
        baseActivity.guardValidSpotifyApi(classBackTo = MainActivity::class.java) { api ->
            Log.i("SpotifyService", this.toString())
            withContext(Dispatchers.IO) {
                api.player.startPlayback(contextUri = playlistUri)
            }
        }
    }

    // Play Track
    suspend fun play(trackURI: PlayableUri) {
        baseActivity.guardValidSpotifyApi(classBackTo = MainActivity::class.java) { api ->
            Log.i("SpotifyService", this.toString())

            withContext(Dispatchers.IO) {
                api.player.startPlayback(playableUrisToPlay = listOf(trackURI))
            }
        }
    }

    suspend fun playTrack(trackURI: PlayableUri) {
        baseActivity.guardValidSpotifyApi(classBackTo = MainActivity::class.java) { api ->
            withContext(Dispatchers.IO) {
                api.player.startPlayback(playableUrisToPlay = listOf(trackURI))
            }
        }
    }

    suspend fun resume() {
        baseActivity.guardValidSpotifyApi(classBackTo = MainActivity::class.java) { api ->
            withContext(Dispatchers.IO) {
                api.player.resume()
            }
        }
    }

    suspend fun pause() {
        baseActivity.guardValidSpotifyApi(classBackTo = MainActivity::class.java) { api ->
            withContext(Dispatchers.IO) {
                api.player.pause()
            }
        }
    }

    suspend fun next() {
        baseActivity.guardValidSpotifyApi(classBackTo = MainActivity::class.java) { api ->
            withContext(Dispatchers.IO) {
                api.player.skipForward()
                api.player.getCurrentContext()!!.item?.asTrack
            }
        }
    }

    suspend fun previous() {
        baseActivity.guardValidSpotifyApi(classBackTo = MainActivity::class.java) { api ->
            withContext(Dispatchers.IO) {
                api.player.skipBehind()
            }
        }
    }

    suspend fun setPositionAndResume(position: Float) {
        baseActivity.guardValidSpotifyApi(classBackTo = MainActivity::class.java) { api ->
            withContext(Dispatchers.IO) {
                api.player.seek(position.toLong() * 1000)
                if (!api.player.getCurrentContext()!!.isPlaying){
                    resume()
                }
                api.player.getCurrentContext()!!.progressMs
            }
        }
    }

    suspend fun getCurrentTrack(callback: (String?, String?) -> Unit) {
        var name: String? = "Name"
        var author: String? = "Author"
        baseActivity.guardValidSpotifyApi(classBackTo = MainActivity::class.java) { api ->
            name = api.player.getCurrentContext()?.item?.asTrack?.name
            author = api.player.getCurrentContext()?.item?.asTrack?.artists?.get(0)?.name
            callback(name, author)
        }
    }

    private suspend fun queueIsEmpty(): Boolean? {
        return baseActivity.guardValidSpotifyApi(classBackTo = MainActivity::class.java) { api ->
            api.player.getUserQueue().queue.isEmpty()
        }
    }

    private suspend fun addRandomTracksToQueue() {
        baseActivity.guardValidSpotifyApi(classBackTo = MainActivity::class.java) { api ->
            val tracks = api.browse.getRecommendations().tracks
            tracks.forEach { api.player.addItemToEndOfQueue(it.uri) }
        }
    }

    suspend fun getSavedTracks(): List<com.adamratzman.spotify.models.SavedTrack>? {
        return baseActivity.guardValidSpotifyApi(classBackTo = MainActivity::class.java) { api ->
            api.library.getSavedTracks().items
        }
    }

    suspend fun getTracks(query: String): List<com.adamratzman.spotify.models.Track>? {
        return withContext(Dispatchers.IO) {
            return@withContext baseActivity.guardValidSpotifyApi(classBackTo = MainActivity::class.java) { api ->
                api.search.searchTrack(query, limit = 15, offset = 0).items
            }
        }
    }

    suspend fun getRecommendedTracks(list: List<String>): List<com.adamratzman.spotify.models.Track>? {
        return withContext(Dispatchers.IO) {
            return@withContext baseActivity.guardValidSpotifyApi(classBackTo = MainActivity::class.java) { api ->
                Log.i("spotifyService", list.joinToString(", "))
                var tracks: List<com.adamratzman.spotify.models.Track>? = null
                if(list.isNotEmpty()) {
                    tracks = api.browse.getTrackRecommendations(
                        seedGenres = list,
                        limit = 10
                    ).tracks
                }
                else{
                    tracks = api.browse.getTrackRecommendations(
                        seedGenres = Constants.GENRES.subList(2,6),
                        limit = 10
                    ).tracks
                }
                tracks
            }
        }
    }

    suspend fun getNewReleases(): List<com.adamratzman.spotify.models.Track>? {
        return withContext(Dispatchers.IO) {
            return@withContext baseActivity.guardValidSpotifyApi(classBackTo = MainActivity::class.java) { api ->
                Log.i("SpotifyService", "Start getNewReleases")
                val result = api.browse.getNewReleases(
                    limit = 5
                )
                var tracks: List<com.adamratzman.spotify.models.Track> = listOf()
                result.forEach{
                    tracks = tracks.plus(it?.toFullAlbum()?.tracks!!.take(5).map { it.toFullTrack()!!})
                }
//                result.forEach {
//                    var track = it?.toFullAlbum()?.tracks!![0].toFullTrack()!!
//                    tracks  = tracks.plus(track)
//                    track = it.toFullAlbum()?.tracks!![1].toFullTrack()!!
//                    tracks  = tracks.plus(track)
//                    track = it.toFullAlbum()?.tracks!![2].toFullTrack()!!
//                    tracks  = tracks.plus(track)
//                    track = it.toFullAlbum()?.tracks!![3].toFullTrack()!!
//                    tracks  = tracks.plus(track)
//                    track = it.toFullAlbum()?.tracks!![4].toFullTrack()!!
//                    tracks  = tracks.plus(track)
//                }
                Log.i("SpotifyService", "Finish getNewReleases ${result}")

                tracks
//                res
//                result.toList()
            }
        }
    }

    suspend fun playableUriToTrack(playableUri: PlayableUri): Track?{
        return withContext(Dispatchers.IO) {
            return@withContext baseActivity.guardValidSpotifyApi(classBackTo = MainActivity::class.java) { api ->
                Log.i("SpotifyService", "Start playableUriToTrack $playableUri")
                val track = api.tracks.getTrack(playableUri.uri)
                Log.i("SpotifyService", "Finish playableUriToTrack")
                track
            }
        }
    }

    suspend fun stringUriToTrack(uri: String): Track {
        return withContext(Dispatchers.IO) {
            return@withContext baseActivity.guardValidSpotifyApi(classBackTo = MainActivity::class.java) { api ->
                Log.i("SpotifyService", "1: stringUriToTrack")
                val track = api.tracks.getTrack(uri)
                Log.i("SpotifyService", "2: stringUriToTrack $track")
                track
            }!!
        }
    }

    suspend fun stringUrisToTracks(uri: List<String>): List<Track?>{
        return withContext(Dispatchers.IO) {
            return@withContext baseActivity.guardValidSpotifyApi(classBackTo = MainActivity::class.java) { api ->
                Log.i("SpotifyService", "1: stringUrisToTracks")
                var track: List<Track?> = listOf()
                if(uri.isNotEmpty()){
                    track = api.tracks.getTracks(*uri.toTypedArray())
                }
                Log.i("SpotifyService", "2: stringUrisToTracks")
                track
            } ?: emptyList()
        }
    }

    suspend fun getCurrentProgress(): Int?{
        return withContext(Dispatchers.IO) {
            return@withContext baseActivity.guardValidSpotifyApi(classBackTo = MainActivity::class.java) { api ->
//                var progressMs = 0
//                Log.i("SpotifyService", "Before progress")
//                try {
//                    progressMs = api.player.getCurrentContext()?.progressMs!!
//                }
//                catch(ex: NullPointerException){
//                    Log.e("SpotifyService", "Error getting progress")
//                }
//                progressMs
                api.player.getCurrentContext()?.progressMs!!
            }
        }
    }

    suspend fun getDevices():List<Device>? {
        return withContext(Dispatchers.IO) {
                Log.i("SpotifyService", baseActivity.toString())
            return@withContext baseActivity.guardValidSpotifyApi(classBackTo = MainActivity::class.java) { api ->
//                Log.i("context", api.player.getDevices().toString())
                api.player.getDevices()
            }
        }
    }

    fun makeToast(message: String) {
        baseActivity.toast(message)
    }

}
