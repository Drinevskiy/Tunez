package com.example.tunez.screens

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.adamratzman.spotify.auth.implicit.startSpotifyImplicitLoginActivity
import com.adamratzman.spotify.auth.pkce.startSpotifyClientPkceLoginActivity
import com.example.tunez.activities.BaseActivity
import com.example.tunez.activities.LoginActivity
import com.example.tunez.activities.RegistrationActivity
import com.example.tunez.activities.Routes
import com.example.tunez.auth.SpotifyImplicitLoginActivityImpl
//import com.example.tunez.auth.SpotifyImplicitLoginActivityImpl
import com.example.tunez.auth.SpotifyPkceLoginActivityImpl
import com.example.tunez.ui.service.SpotifyService
import com.example.tunez.utils.toast
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.database

@Composable
fun ProfileScreen(spotifyService: SpotifyService, activity: BaseActivity, navController: NavController, modifier: Modifier = Modifier) {
    val user = Firebase.auth.currentUser
//    activity.toast(user?.email.toString())
    Column {
        if (user == null) {
            Button(onClick = {
                activity.startSpotifyClientPkceLoginActivity(SpotifyPkceLoginActivityImpl::class.java)
            }) {
                Text("PKCE")
            }
            Button(onClick = {
//                activity.startSpotifyImplicitLoginActivity(SpotifyImplicitLoginActivityImpl::class.java)
            }) {
                Text("Implicit")
            }
            Button(onClick = {
                activity.startActivity(Intent(activity, LoginActivity::class.java))
            }) {
                Text("Login")
            }
            Button(onClick = {
                activity.startActivity(Intent(activity, RegistrationActivity::class.java))
            }) {
                Text("Registration")
            }
        }
        else {
            var role by remember { mutableStateOf("user")}
            var username by remember { mutableStateOf("username")}
            var genres by remember { mutableStateOf(emptyList<String>())}
            Firebase.database.reference.child("Users").child(user.uid).child("role").get().addOnSuccessListener {
                role = it.value.toString()
            }.addOnFailureListener{
                Log.e("firebase", "Error getting data", it)
            }
            Firebase.database.reference.child("Users").child(user.uid).child("genres").get().addOnSuccessListener {
                genres = it.value as List<String>
            }.addOnFailureListener{
                Log.e("firebase", "Error getting data", it)
            }
            Firebase.database.reference.child("Users").child(user.uid).child("username").get().addOnSuccessListener {
                username = it.value.toString()
            }.addOnFailureListener{
                Log.e("firebase", "Error getting data", it)
            }
            Text(text = "Username: $username")
            Text(text = "Email: ${user.email}")
            Text(text = "Status: $role")
            Text(text = "Favourite genres: ${genres.joinToString(", ")}")
            Button(onClick = {
                Firebase.auth.signOut()
                navController.navigate(Routes.Home.route) {
                    popUpTo(navController.graph.findStartDestination().id)
                }
            }) {
                Text("Logout")
            }
        }
    }
}
