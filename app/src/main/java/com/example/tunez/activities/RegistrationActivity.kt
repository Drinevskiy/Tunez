package com.example.tunez.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.tunez.data.Constants
import com.example.tunez.ui.theme.TunezTheme
import com.example.tunez.utils.toast
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.database

class RegistrationActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TunezTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    RegistrationPage(this)
                }
            }
        }
    }
}

@Composable
fun RegistrationPage(activity: Activity, modifier: Modifier = Modifier){
    val db = Firebase.database.reference
    var username by remember{ mutableStateOf("") }
    var email by remember{ mutableStateOf("") }
    var password by remember{ mutableStateOf("") }
    var isChecked by remember { mutableStateOf(false) }
    var role = "user"
    val (genres, updateGenres) = remember { mutableStateOf(emptyList<String>())}
        Column {
        Row(modifier = modifier.padding(10.dp)) {
            Icon(imageVector = Icons.Default.AccountCircle, contentDescription = "")
            Spacer(modifier = modifier.width(8.dp))
            TextField(value = username,
                onValueChange = {username = it},
                label = { Text("Username") })
        }
        Row(modifier = modifier.padding(10.dp)) {
            Icon(imageVector = Icons.Default.Email, contentDescription = "")
            Spacer(modifier = modifier.width(8.dp))
            TextField(value = email,
                onValueChange = {email = it},
                label = { Text("Email") })
        }
        Row(modifier = modifier.padding(10.dp)) {
            Icon(imageVector = Icons.Default.Lock, contentDescription = "")
            Spacer(modifier = modifier.width(8.dp))
            TextField(value = password,
                onValueChange = {password = it},
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password
                )
            )
        }
        Row(modifier = modifier.padding(10.dp)){
            Icon(imageVector = Icons.Default.Favorite, contentDescription = "")
            Spacer(modifier = modifier.width(8.dp))
            SelectableDropdownMenu(Constants.GENRES, genres, {updateGenres(it)})
        }
        Row(modifier = modifier.padding(10.dp)) {
            Text("Artist")
            Checkbox(
                checked = isChecked,
                onCheckedChange = { isChecked = it }
            )
        }
        Button(
            onClick={
                if(username.isEmpty()||email.isEmpty()||password.isEmpty()||genres.isEmpty()){
                    activity.toast("Введены некорректные данные")
                }
                else{
                    Firebase.auth.createUserWithEmailAndPassword(email,password)
                        .addOnCompleteListener{
                            if(it.isSuccessful){
                                if(isChecked){
                                    role = "artist"
                                }
                                val userinfo = mapOf(
                                    "username" to username,
                                    "email" to email,
                                    "role" to role,
                                    "genres" to genres
                                )

                                    db.child("Users")
                                    .child(Firebase.auth.currentUser!!.uid)
                                    .setValue(userinfo)
//                                    activity.startActivity(Intent(activity, SpotifyPkceLoginActivityImpl::class.java))
                                    activity.startActivity(Intent(activity, MainActivity::class.java))
                            }
                        }

                }
            }
        ){
            Text(text="Sign up")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectableDropdownMenu(names: List<String>, selectedNames: List<String>, onListUpdated: (List<String>) -> Unit) {
    var isExpanded by remember { mutableStateOf(false) }
    val (list, setList) = remember { mutableStateOf(selectedNames) }
    ExposedDropdownMenuBox(
        expanded = isExpanded,
        onExpandedChange = { isExpanded = it }
    ) {
        TextField(
            value = selectedNames.joinToString(", "),
            onValueChange = {},
            placeholder = {
                Text(text = "Select a genres")
            },
            readOnly = true, // Makes the TextField clickable
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded)
            },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
            modifier = Modifier.menuAnchor() // Needed to anchor the dropdown menu
        )
        ExposedDropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false }
        ) {
            names.forEach { name ->
                AnimatedContent(
                    targetState = selectedNames.contains(name),
                    label = "Animate the selected item"
                ) { isSelected ->
                    if (isSelected) {
                        DropdownMenuItem(
                            text = {
                                Text(text = name)
                            },
                            onClick = {
                                setList(list - name)
                                onListUpdated(list - name)
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Rounded.Check,
                                    contentDescription = null
                                )
                            }
                        )
                    } else {
                        DropdownMenuItem(
                            text = {
                                Text(text = name)
                            },
                            onClick = {
                                setList(list + name)
                                onListUpdated(list + name)
                            },
                        )
                    }
                }
            }
        }

    }
}