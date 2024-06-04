package com.example.tunez.activities

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.adamratzman.spotify.models.Track
import com.example.tunez.SpotifyPlaygroundApplication
import com.example.tunez.content.Playlist
import com.example.tunez.screens.AddPlaylistScreen
import com.example.tunez.screens.AddTrackScreen
import com.example.tunez.screens.ArtistProfileScreen
import com.example.tunez.screens.ChoosePlaylistScreen
import com.example.tunez.screens.EditTrackScreen
import com.example.tunez.screens.HomeScreen
import com.example.tunez.screens.InfoTrackForAdminScreen
import com.example.tunez.screens.PlaylistScreen
import com.example.tunez.screens.ProfileInfoScreen
import com.example.tunez.screens.ProfileScreen
import com.example.tunez.screens.RecommendationsScreen
import com.example.tunez.screens.ReleasesScreen
import com.example.tunez.screens.SearchScreen
import com.example.tunez.screens.UserPlaylistScreen
import com.example.tunez.ui.theme.TunezTheme
import com.example.tunez.viewmodels.NavControllerViewModel
import com.example.tunez.viewmodels.UserInfo
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import kotlinx.serialization.json.Json
import org.koin.androidx.compose.inject
import kotlin.reflect.KProperty0

var user: FirebaseUser? = Firebase.auth.currentUser
class MainActivity : BaseActivity() {

    private val myApplication: SpotifyPlaygroundApplication
        get() = application as SpotifyPlaygroundApplication
    val loadImage = registerForActivityResult(ActivityResultContracts.GetContent()){
        Log.i("AddTrackScreen", it.toString())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myApplication.spotifyService.baseActivity = this
        setContent {
            TunezTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    NavPage(this)
                }
            }
        }

    }

    override fun onStop() {
        super.onStop()
        val TAG: String = "MainActivity"
        Log.d(TAG, "Остановка")
    }

    override fun onDestroy() {
        super.onDestroy()
        val TAG: String = "MainActivity"
        Log.d(TAG, "Уничтожение")
    }
}

@Composable
fun NavPage(activity: BaseActivity) {
//    spotifyService.getDevices()
    val vm: NavControllerViewModel by inject()
    val navController = rememberNavController()
//    LaunchedEffect(navController) {
//    vm.setNavController(navController)
//        vm.goToProfile()
//    }
    vm.setNavController(navController)
    val uiState by vm.navUiState.collectAsState()

    Column {
        NavHost(navController = navController, startDestination = Routes.Home.route, modifier = Modifier.weight(1f)) {
            composable(Routes.Home.route) {
                HomeScreen()
            }

            composable(Routes.Search.route) {
                SearchScreen()
            }

            composable(Routes.Recommendations.route) {
                RecommendationsScreen()
            }

            composable(Routes.Releases.route) {
                ReleasesScreen()
            }

            composable(Routes.Profile.route) {
                ProfileScreen(activity)
            }
            composable(Routes.Playlist.route + "?name={name}&durationInMs={durationInMs}&image={image}&tracks={tracks}&id={id}",
                arguments = listOf(
                    navArgument("name") { type = NavType.StringType },
                    navArgument("durationInMs") { type = NavType.IntType },
                    navArgument("image") { type = NavType.StringType },
                    navArgument("tracks") { type = NavType.StringType },
                    navArgument("id") { type = NavType.StringType }
                )
            ){
                val name = it.arguments?.getString("name") ?: "No name"
                val durationInMs = it.arguments?.getInt("durationInMs") ?: 0
                var image: String? = null
                if(it.arguments?.getString("image") != "nullable") {
                    image = it.arguments?.getString("image")
                }
                val tracksSerialized = it.arguments?.getString("tracks")
                val tracks = Json.decodeFromString<List<Track>>(tracksSerialized!!)
                val id = it.arguments?.getString("id")
                val playlist = Playlist(
                    durationInMs = durationInMs,
                    name = name,
                    tracks = tracks,
                    image = image,
                    id = id
                )
                PlaylistScreen(playlist = playlist)
            }
            composable(Routes.AddPlaylist.route) {
                AddPlaylistScreen()
            }
            composable(Routes.ChoosePlaylist.route + "?uri={uri}",
                arguments = listOf(
                    navArgument("uri"){ type = NavType.StringType}
                )
            ) {
                val uri = it.arguments?.getString("uri") ?: ""
                ChoosePlaylistScreen(uri)
            }
            composable(Routes.ProfileInfo.route + "?username={username}&email={email}&role={role}&uid={uid}",
                arguments = listOf(
                    navArgument("username") { type = NavType.StringType },
                    navArgument("email") { type = NavType.StringType },
                    navArgument("role") { type = NavType.StringType },
                    navArgument("uid") { type = NavType.StringType }
                )) {
                val username = it.arguments?.getString("username") ?: "No name"
                val email = it.arguments?.getString("email") ?: "No email"
                val role = it.arguments?.getString("role") ?: "user"
                val uid = it.arguments?.getString("uid") ?: "No uid"
                val userInfo = UserInfo(uid, username, email, role)
                ProfileInfoScreen(userInfo = userInfo)
            }
            composable(Routes.UserPlaylist.route + "?name={name}&durationInMs={durationInMs}&image={image}&tracks={tracks}&id={id}",
                arguments = listOf(
                    navArgument("name") { type = NavType.StringType },
                    navArgument("durationInMs") { type = NavType.IntType },
                    navArgument("image") { type = NavType.StringType },
                    navArgument("tracks") { type = NavType.StringType },
                    navArgument("id") { type = NavType.StringType }
                )
            ){
                val name = it.arguments?.getString("name") ?: "No name"
                val durationInMs = it.arguments?.getInt("durationInMs") ?: 0
                var image: String? = null
                if(it.arguments?.getString("image") != "nullable") {
                    image = it.arguments?.getString("image")
                }
                val tracksSerialized = it.arguments?.getString("tracks")
                val tracks = Json.decodeFromString<List<Track>>(tracksSerialized!!)
                val id = it.arguments?.getString("id")
                val playlist = Playlist(
                    durationInMs = durationInMs,
                    name = name,
                    tracks = tracks,
                    image = image,
                    id = id
                )
                UserPlaylistScreen(playlist = playlist)
            }
            composable(Routes.AddTrack.route) {
                AddTrackScreen(activity)
            }
            composable(Routes.EditTrack.route + "?name={name}&blocked={blocked}&edited={edited}&reason={reason}&count={count}&id={id}&artistId={artistId}",
                arguments = listOf(
                    navArgument("name") { type = NavType.StringType },
                    navArgument("blocked") { type = NavType.BoolType },
                    navArgument("edited") { type = NavType.BoolType },
                    navArgument("reason") { type = NavType.StringType },
                    navArgument("count") { type = NavType.LongType },
                    navArgument("id") { type = NavType.StringType }
                )) {
                val name = it.arguments?.getString("name") ?: "No name"
                val blocked = it.arguments?.getBoolean("blocked") ?: false
                val edited = it.arguments?.getBoolean("edited") ?: false
                val reason = it.arguments?.getString("reason") ?: ""
                val count = it.arguments?.getLong("count") ?: 0L
                val id = it.arguments?.getString("id")
                val artistId = it.arguments?.getString("artistId")
                val track = com.example.tunez.content.Track(id, name, blocked, edited, reason, count, artistId)
                EditTrackScreen(track)
            }
            composable(Routes.InfoTrackForAdmin.route + "?name={name}&blocked={blocked}&edited={edited}&reason={reason}&id={id}&artistId={artistId}",
                arguments = listOf(
                    navArgument("name") { type = NavType.StringType },
                    navArgument("blocked") { type = NavType.BoolType },
                    navArgument("edited") { type = NavType.BoolType },
                    navArgument("reason") { type = NavType.StringType },
                    navArgument("id") { type = NavType.StringType },
                    navArgument("artistId") { type = NavType.StringType }
                )) {
                val name = it.arguments?.getString("name") ?: "No name"
                val blocked = it.arguments?.getBoolean("blocked") ?: false
                val edited = it.arguments?.getBoolean("edited") ?: false
                val reason = it.arguments?.getString("reason") ?: ""
                val id = it.arguments?.getString("id")
                val artistId = it.arguments?.getString("artistId")
                val track = com.example.tunez.content.Track(id, name, blocked, edited, reason, artistId = artistId)
                InfoTrackForAdminScreen(track)
            }
            composable(Routes.ArtistProfile.route + "?username={username}&email={email}&role={role}&uid={uid}",
                arguments = listOf(
                    navArgument("username") { type = NavType.StringType },
                    navArgument("email") { type = NavType.StringType },
                    navArgument("role") { type = NavType.StringType },
                    navArgument("uid") { type = NavType.StringType }
                )) {
                val username = it.arguments?.getString("username") ?: "No name"
                val email = it.arguments?.getString("email") ?: "No email"
                val role = it.arguments?.getString("role") ?: "user"
                val uid = it.arguments?.getString("uid") ?: "No uid"
                val userInfo = UserInfo(uid, username, email, role)
                ArtistProfileScreen(userInfo = userInfo)
            }
        }
        BottomNavigationBar(navController)
    }
}

@Composable
fun BottomNavigationBar(navController: NavController){
    NavigationBar {
        val backStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = backStackEntry?.destination?.route
        NavBarItems.BarItems.forEach { navItem ->
            NavigationBarItem(
                selected = currentRoute == navItem.route,
                onClick = {
                    navController.navigate(navItem.route) {
                        popUpTo(navController.graph.findStartDestination().id) {saveState = true}
                        launchSingleTop = true
                        restoreState = true
                        if (navItem.route == "profile") {
                            restoreState = false
                        }
                    }
                },
                icon = {
                    Icon(imageVector = navItem.image,
                        contentDescription = navItem.title)
                },
                label = {
                    Text(text = navItem.title)
                }
            )
        }
    }
}

object NavBarItems {
    val BarItems = listOf(
        BarItem(
            title = "Home",
            image = Icons.Default.Home,
            route = "home"
        ),
        BarItem(
            title = "Search",
            image = Icons.Default.Search,
            route = "search"
        ),
        BarItem(
            title = "Releases",
            image = Icons.Default.List,
            route = "releases"
        ),
        BarItem(
            title = "Recs",
            image = Icons.Default.AddCircle,
            route = "recs"
        ),
        BarItem(
            title = "Profile",
            image = Icons.Default.Person,
            route = "profile"
        )
    )
}

data class BarItem(
    val title: String,
    val image: ImageVector,
    val route: String
)

sealed class Routes(val route: String) {
    object Home : Routes("home")
    object Search : Routes("search")
    object Recommendations : Routes("recs")
    object Releases : Routes("releases")
    object Profile : Routes("profile")
    object Playlist : Routes("playlist")
    object AddPlaylist : Routes("add-playlist")
    object ChoosePlaylist : Routes("choose-playlist")
    object UserPlaylist : Routes("user-playlist")
    object ProfileInfo : Routes("profile-info")
    object AddTrack : Routes("add-track")
    object EditTrack : Routes("edit-track")
    object InfoTrackForAdmin : Routes("info-track-for-admin")
    object ArtistProfile : Routes("artist-profile")

}
