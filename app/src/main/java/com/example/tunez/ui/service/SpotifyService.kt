package com.example.tunez.ui.service

import android.util.Log
import com.adamratzman.spotify.models.ContextUri
import com.adamratzman.spotify.models.PlayableUri
import com.adamratzman.spotify.models.RecommendationSeed
import com.adamratzman.spotify.models.Track
import com.adamratzman.spotify.utils.Market
import com.example.tunez.activities.ActionHomeActivity
import com.example.tunez.activities.BaseActivity
import com.example.tunez.activities.MainActivity
import com.example.tunez.auth.guardValidSpotifyApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext


class SpotifyService() {
    lateinit var baseActivity: BaseActivity
    //    private val CLIENT_ID = "9f8034d574a341afad46a90ccaf04f04"
//    private val REDIRECT_URI = "com.example.tunez://callback"
//    private var spotifyAppRemote: SpotifyAppRemote? = null
//    lateinit var spotifyApi: SpotifyClientApi
//    val codeVerifier = "h93739749c349h938495h39h593h459uvc6geu5owGUS7DFU5FY38735783Grg83"
//    val codeChallenge = getSpotifyPkceCodeChallenge(codeVerifier) // helper method
//    val url: String = getSpotifyAuthorizationUrl(
//        SpotifyScope.PlaylistReadPrivate,
//        SpotifyScope.PlaylistModifyPrivate,
//        SpotifyScope.UserFollowRead,
//        SpotifyScope.UserLibraryModify,
//        clientId = CLIENT_ID,
//        redirectUri = REDIRECT_URI,
//    )
//
//    val connectionParams = ConnectionParams.Builder(CLIENT_ID)
//        .setRedirectUri(REDIRECT_URI)
//        .showAuthView(true)
//        .build()
//
//    constructor(context: Context, activity: BaseActivity) {
//        SpotifyAppRemote.connect(context, connectionParams, object : Connector.ConnectionListener {
//            override fun onConnected(appRemote: SpotifyAppRemote) {
//                spotifyAppRemote = appRemote
//                Log.d("MainActivity", "Connected! Yay!")
//                // Now you can start interacting with App Remote
//                //connected()
//            }
//
//            override fun onFailure(throwable: Throwable) {
//                Log.e("MainActivity", throwable.message, throwable)
//                // Something went wrong when attempting to connect! Handle errors here
//            }
//        })
//        baseActivity = activity
//        runBlocking {
//            spotifyApi = spotifyClientPkceApi(
//                CLIENT_ID, // optional. include for token refresh
//                REDIRECT_URI, // optional. include for token refresh
//                SpotifyUserAuthorization(
//                    tokenString = "BQChdXxqB5zUnQJD7ECW1Oimat-1fzFpoF5OjmdPie-5FGC6OvI1gqsaJbpLxV-F_rqYzKtg3K6NXGEDC6vMMZPS-ff7CkGsk83h5VjgSAgFAIBPHAyrj_ss5cDtCivCdPvB7CXuBpOKb2PYISfiO-yI6qkbUDIM1anEErL88tqcQ2_mdEh5WiFqqXuiSI4Ti909lJ-L2Sg6flsMCY-AoKiFpWiPo5IAMQ",
//                    pkceCodeVerifier = codeVerifier
//                )
////                 the same code verifier you used to generate the code challenge
//            ) {
//                retryWhenRateLimited = false
//                automaticRefresh = true
//                refreshTokenProducer
//            }.build()
//        }
//    }
//    private fun connected() {
//        spotifyAppRemote?.let {
//            it.playerApi.subscribeToPlayerState().setEventCallback {
//                val track: Track = it.track
//                Log.d("MainActivity", track.name + " by " + track.artist.name)
//            }
//        }
//
//    }
    // Play Playlist
    suspend fun play(playlistUri: ContextUri) {
        baseActivity.guardValidSpotifyApi(classBackTo = MainActivity::class.java) { api ->
            withContext(Dispatchers.IO) {
                api.player.startPlayback(contextUri = playlistUri)
            }
        }
    }

    // Play Track
    suspend fun play(trackURI: PlayableUri) {
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

                api.browse.getTrackRecommendations(
                    seedGenres = list,
                    limit = 10
                ).tracks
            }
        }
    }

    suspend fun getNewReleases(): List<com.adamratzman.spotify.models.Track>? {
        return withContext(Dispatchers.IO) {
            return@withContext baseActivity.guardValidSpotifyApi(classBackTo = MainActivity::class.java) { api ->
                val result = api.browse.getNewReleases(
                    limit = 5
                )
//                val res = api.browse.getFeaturedPlaylists(limit = 5).playlists.get(2).toFullPlaylist()?.tracks?.items?.take(15)?.map { it.track!!.asTrack!! }
//                val result = api.personalization.getTopTracks()
                var tracks: List<com.adamratzman.spotify.models.Track> = listOf()
                result.forEach {
                    var track = it?.toFullAlbum()?.tracks!![0].toFullTrack()!!
                    tracks  = tracks.plus(track)
                    track = it.toFullAlbum()?.tracks!![1].toFullTrack()!!
                    tracks  = tracks.plus(track)
                    track = it.toFullAlbum()?.tracks!![2].toFullTrack()!!
                    tracks  = tracks.plus(track)
                    track = it.toFullAlbum()?.tracks!![3].toFullTrack()!!
                    tracks  = tracks.plus(track)
                    track = it.toFullAlbum()?.tracks!![4].toFullTrack()!!
                    tracks  = tracks.plus(track)
                }
                tracks
//                res
//                result.toList()
            }
        }
    }

    suspend fun playableUriToTrack(playableUri: PlayableUri): Track?{
        return withContext(Dispatchers.IO) {
            return@withContext baseActivity.guardValidSpotifyApi(classBackTo = MainActivity::class.java) { api ->
                api.tracks.getTrack(playableUri.uri)
            }
        }
    }

    suspend fun getCurrentProgress(): Int?{
        return withContext(Dispatchers.IO) {
            return@withContext baseActivity.guardValidSpotifyApi(classBackTo = MainActivity::class.java) { api ->
//                val currentContext = api.player.getCurrentContext()
//                Log.i("context", currentContext.toString())
//                var progressMs: Int = 0
//                if (currentContext != null) {
//                    Log.i("context", "not null")
//
//                    progressMs = currentContext.progressMs!!
//                }
//                progressMs
                api.player.getCurrentContext()?.progressMs
            }
        }
    }

    fun getDevices() {
        baseActivity.guardValidSpotifyApi(classBackTo = MainActivity::class.java) { api ->
            withContext(Dispatchers.IO) {
                Log.i("context", api.player.getDevices().toString())
            }
        }
    }
}
