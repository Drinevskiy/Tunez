package com.example.tunez.screens

import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tunez.activities.BaseActivity
import com.example.tunez.activities.MainActivity
import com.example.tunez.content.Track
import com.example.tunez.viewmodels.NavControllerViewModel
import com.example.tunez.viewmodels.ProfileViewModel
import org.koin.androidx.compose.inject
import kotlin.reflect.KProperty0

@Composable
fun AddTrackScreen(
    activity: BaseActivity,
){
    var name by remember { mutableStateOf("") }
    val vmController: NavControllerViewModel by inject()
    val vm: ProfileViewModel by inject()
    val keyboardController = LocalSoftwareKeyboardController.current
    val trailingIcon = @Composable {
        IconButton(
            onClick = {
                name = ""
            }) {
            Icon(imageVector = Icons.Default.Close, contentDescription = "")
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(0.8f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Enter the name of the track",
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                modifier = Modifier
                    .padding(0.dp, 20.dp)
                //                .fillMaxWidth()
            )
            TextField(
                value = name,
                onValueChange = { name = it },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                leadingIcon = {
                    Icon(imageVector = Icons.Default.List, contentDescription = "")
                },
                trailingIcon = if (name.isEmpty()) null else trailingIcon,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 15.dp)
            )
            Row(modifier = Modifier.fillMaxWidth()) {
                Button(
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp),
                    onClick = {
                        vmController.goBack()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Gray,
                        contentColor = Color.White
                    )
                ) {
                    Text(text = "Cancel")
                }
                Button(
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp),
                    onClick = {
                        keyboardController?.hide()
                        vmController.goBack()
//                        vm.addPlaylist(name)
                        vm.addTrackToArtistProfile(name)
                        vm.makeToast("$name added to your profile")
                    },
                ) {
                    Text(text = "Add")
                }
            }
        }
    }
}

@Composable
fun EditTrackScreen(track: Track){
    var name by remember { mutableStateOf(track.name) }
    val vmController: NavControllerViewModel by inject()
    val vm: ProfileViewModel by inject()
    val keyboardController = LocalSoftwareKeyboardController.current
    val trailingIcon = @Composable {
        IconButton(
            onClick = {
                name = ""
            }) {
            Icon(imageVector = Icons.Default.Close, contentDescription = "")
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(0.8f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Enter the name of the track",
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                modifier = Modifier
                    .padding(0.dp, 20.dp)
                //                .fillMaxWidth()
            )
            if(track.blocked){
                Text(
                    text = "Track blocked. Reason: ${track.reason}",
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 16.sp,
                    modifier = Modifier
                        .padding(bottom = 20.dp)
                )
            }
            if(track.edited && track.blocked){
                Text(
                    text = "Changes are being considered. Please wait",
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 16.sp,
                    modifier = Modifier
                        .padding(bottom = 20.dp)
                )
            }
            var listenText = "Listened to ${track.count} time"
            if(track.count > 1){
                listenText+="s"
            }
            Text(
                text = listenText,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                modifier = Modifier
                    .padding(bottom = 20.dp)
                //                .fillMaxWidth()
            )
            TextField(
                value = "$name",
                onValueChange = { name = it },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                leadingIcon = {
                    Icon(imageVector = Icons.Default.List, contentDescription = "")
                },
                trailingIcon = if (name!!.isEmpty()) null else trailingIcon,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 15.dp)
            )
            Row(modifier = Modifier.fillMaxWidth()) {
                Button(
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp),
                    onClick = {
                        vmController.goBack()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Gray,
                        contentColor = Color.White
                    )
                ) {
                    Text(text = "Cancel")
                }
                Button(
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp),
                    onClick = {
                        keyboardController?.hide()
                        vmController.goBack()
                        val newTrack = Track(track.id, name, track.blocked, track.edited, track.reason, track.count, track.artistId)
                        vm.changeArtistTrack(newTrack)
                        vm.makeToast("$name edited")
                    },
                ) {
                    Text(text = "Edit")
                }
            }
        }
    }
}

@Composable
fun InfoTrackForAdminScreen(track: com.example.tunez.content.Track){
    var reason by remember { mutableStateOf("") }
    val vmController: NavControllerViewModel by inject()
    val vm: ProfileViewModel by inject()
    val keyboardController = LocalSoftwareKeyboardController.current
    val trailingIcon = @Composable {
        IconButton(
            onClick = {
                reason = ""
            }) {
            Icon(imageVector = Icons.Default.Close, contentDescription = "")
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(
                onClick = {
                    vmController.goBack()
                }
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = null,
                    modifier = Modifier.size(30.dp, 30.dp)
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "${track.name}",
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                modifier = Modifier
                    .padding(0.dp, 20.dp)
            )
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = {
                vm.playArtistTrack(track)
            }) {
                Icon(imageVector = Icons.Filled.PlayArrow, contentDescription = null)
            }
        }
    }
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(
                modifier = Modifier.fillMaxWidth(0.8f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (track.blocked) {
                    Text(
                        text = "Track blocked. Reason: ${track.reason}",
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Normal,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 16.sp,
                        modifier = Modifier
                            .padding(bottom = 20.dp)
                    )
                }
                var text = "Artist has not made any changes yet"
                if (track.edited) {
                    text = "Artist change name to ${track.name}"
                }
                Text(
                    text = text,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 16.sp,
                    modifier = Modifier
                        .padding(bottom = 20.dp)
                )
                TextField(
                    value = reason,
                    onValueChange = { reason = it },
                    singleLine = true,
                    placeholder = { Text("Ban reason") },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.List, contentDescription = "")
                    },
                    trailingIcon = if (reason.isEmpty()) null else trailingIcon,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 15.dp)
                )
                Column(modifier = Modifier.fillMaxWidth()) {
                    if(track.blocked) {
                        Button(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp)
                                .padding(0.dp, 8.dp),
                            onClick = {
                                keyboardController?.hide()
                                vmController.goBack()
                                vm.unbanArtistTrack(track)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        ) {
                            Text(text = "Unban")
                        }
                    }
                    Button(
                        modifier = Modifier
                            .height(60.dp)
                            .fillMaxWidth()
                            .padding(0.dp, 8.dp),
                        onClick = {
                            keyboardController?.hide()
                            vmController.goBack()
                            vm.banArtistTrack(track, reason)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.tertiary,
                            contentColor = MaterialTheme.colorScheme.onTertiary
                        )

                    ) {
                        Text(text = "Ban")
                    }
                    Button(
                        modifier = Modifier
                            .height(60.dp)
                            .fillMaxWidth()
                            .padding(0.dp, 8.dp),
                        onClick = {
                            keyboardController?.hide()
                            vmController.goBack()
                            vm.deleteArtistTrack(track)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                        )
                    ) {
                        Text(text = "Delete")
                    }
                }
            }
//        }
    }
}