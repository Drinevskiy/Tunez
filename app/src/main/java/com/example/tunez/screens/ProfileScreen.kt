package com.example.tunez.screens

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.tunez.activities.BaseActivity
import com.example.tunez.activities.LoginActivity
import com.example.tunez.activities.Routes
import com.example.tunez.activities.user
import com.example.tunez.viewmodels.AppViewModelProvider
import com.example.tunez.viewmodels.ProfileViewModel
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.database

@Composable
fun ProfileScreen(activity: BaseActivity, navController: NavController, modifier: Modifier = Modifier, vm: ProfileViewModel = viewModel(factory = AppViewModelProvider.Factory)) {
    val uiState by vm.profileUiState.collectAsState()
    val scope = rememberCoroutineScope()
    var shouldLaunchLoginActivity by remember { mutableStateOf(user == null) }

    if(shouldLaunchLoginActivity) {
        LaunchedEffect(shouldLaunchLoginActivity) {
            activity.startActivity(Intent(activity, LoginActivity::class.java))
        }
    }
    else {
        vm.getAllInfo()
        Row {
            Text(
                text = "Profile",
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                modifier = Modifier
                    .padding(0.dp, 20.dp)
                    .fillMaxWidth()
            )
        }
        Row(
            horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth().padding(10.dp)
        ) {
            IconButton(
                onClick = {
                    Firebase.auth.signOut()
                    user = null
                    navController.navigate(Routes.Home.route) {
                        popUpTo(navController.graph.findStartDestination().id)
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.ExitToApp,
                    contentDescription = null,
                    modifier = Modifier.size(35.dp, 35.dp)
                )
            }
        }
        Column(
            modifier = Modifier
                .padding(start = 20.dp, top = 80.dp, end = 20.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = "Username: ${uiState.username}",
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                ),
                modifier = Modifier.padding(bottom = 8.dp)

            )
            Text(
                text = "Email: ${uiState.email}",
                style = TextStyle(
                    fontSize = 16.sp,
                    color = Color.Gray,
                ),
                modifier = Modifier.padding(bottom = 8.dp)

            )
            Text(
                text = "Status: ${uiState.role}",
                style = TextStyle(
                    fontSize = 16.sp,
                    color = Color.Gray,
                ),
                modifier = Modifier.padding(bottom = 8.dp)

            )
            Text(
                text = "Favourite genres: ${uiState.genres.joinToString(", ")}",
                style = TextStyle(
                    fontSize = 16.sp,
                    color = Color.Gray,
                ),
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        vm.getAllUsers()
    }
}


