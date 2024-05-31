package com.example.tunez.activities

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.viewModels
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.tunez.R
import com.example.tunez.SpotifyPlaygroundApplication
import com.example.tunez.content.Playlist
import com.example.tunez.screens.HomeScreen
import com.example.tunez.screens.ProfileScreen
import com.example.tunez.screens.RecommendationsScreen
import com.example.tunez.screens.ReleasesScreen
import com.example.tunez.screens.SearchScreen
import com.example.tunez.ui.service.SpotifyService
import com.example.tunez.ui.theme.TunezTheme
import com.example.tunez.viewmodels.AppViewModelProvider
import com.example.tunez.viewmodels.NavControllerViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.gson.Gson
import org.koin.androidx.compose.inject

var user: FirebaseUser? = Firebase.auth.currentUser
class MainActivity : BaseActivity() {
    val myApplication: SpotifyPlaygroundApplication
        get() = application as SpotifyPlaygroundApplication
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myApplication.spotifyService.baseActivity = this
//        myApplication.spotifyService.getDevices()
            setContent {
                TunezTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        NavPage(this, myApplication.spotifyService)
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
fun NavPage(activity: BaseActivity, spotifyService: SpotifyService) {
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
                ProfileScreen(activity, navController)
            }
            composable(Routes.Playlist.route + "?name={name}&durationInMs={durationInMs}&image={image}&tracks={tracks}",
                arguments = listOf(
                    navArgument("name") { type = NavType.StringType },
                    navArgument("durationInMs") { type = NavType.IntType },
                    navArgument("image") { type = NavType.StringType },
                    navArgument("tracks") { type = NavType.StringType }
                )
            ){
                val name = it.arguments?.getString("name") ?: "No name"
                val durationInMs = it.arguments?.getInt("durationInMs") ?: 0
                val image = it.arguments?.getString("image")
                val tracks = it.arguments?.getString("tracks")?.split(",") ?: emptyList()
                val playlist = Playlist(
                    durationInMs = durationInMs,
                    name = name,
                    tracks = tracks,
                    image = image
                )
                PlaylistScreen(playlist = playlist, spotifyService = spotifyService, activity = activity)
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
}
