package com.example.tunez.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tunez.data.Constants
import com.example.tunez.data.Model
import com.example.tunez.ui.theme.TunezTheme
import com.example.tunez.utils.toast
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
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
                    RegistrationPage(activity = this)
                }
            }
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
            leadingIcon = {
                Icon(imageVector = Icons.Default.Favorite, contentDescription = "")
            },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded)
            },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
            modifier = Modifier
                .menuAnchor()
                .padding(10.dp)
//                .fillMaxWidth(0.8f) // Needed to anchor the dropdown menu
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

@Composable
fun RegistrationPage(modifier: Modifier = Modifier, activity: Activity? = null){
    val db = Firebase.database.reference
    var username by remember{ mutableStateOf("") }
    var email by remember{ mutableStateOf("") }
    var password by remember{ mutableStateOf("") }
    var passwordConfirm by remember{ mutableStateOf("") }
    var isArtist by remember { mutableStateOf(false) }
    val (genres, updateGenres) = remember { mutableStateOf(emptyList<String>())}

    var emailError by remember{ mutableStateOf("") }
    var passwordError by remember{ mutableStateOf("") }
    var passwordConfirmError by remember{ mutableStateOf("") }
    var genresError by remember{ mutableStateOf("") }
    var isCanTryRegister by remember{ mutableStateOf(true) }

    var role = "user"
    var isShowAlertDialog by remember{ mutableStateOf(false) }
    if(isShowAlertDialog){
        ErrorAlert(
            title = "Error",
            message = "This email is already occupied. Try use another email",
            onDismissRequest = { isShowAlertDialog = false },
            onConfirm = { isShowAlertDialog = false }
        )
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .padding(bottom = 10.dp)
            .verticalScroll(
                rememberScrollState()
            )
    ) {
    Row() {
        Text(
            text = "Registration",
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            modifier = Modifier
                .padding(0.dp, 10.dp)
                .fillMaxWidth()
        )
    }
        Column(modifier = modifier.padding(10.dp)) {
            TextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = ""
                    )
                },
                modifier = Modifier
                    .padding(10.dp)
            )
        }
        Column(modifier = modifier.padding(5.dp)) {
            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                leadingIcon = { Icon(imageVector = Icons.Default.Email, contentDescription = "") },
                modifier = Modifier
                    .padding(10.dp)
            )
            if (emailError.isNotEmpty()) {
                Text(
                    text = emailError,
                    color = Color.Red
                )
            }

        }
        Column(modifier = modifier.padding(5.dp)) {
            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password
                ),
                leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = "") },
                modifier = Modifier
                    .padding(10.dp)
            )
            if (passwordError.isNotEmpty()) {
                Text(
                    text = passwordError,
                    color = Color.Red
                )
            }
        }
        Column(modifier = modifier.padding(5.dp)) {
            TextField(
                value = passwordConfirm,
                onValueChange = { passwordConfirm = it },
                label = { Text("Confirm password") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password
                ),
                leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = "") },
                modifier = Modifier
                    .padding(10.dp)
            )
            Box(modifier = modifier) {
                if (passwordConfirmError.isNotEmpty()) {
                    Text(
                        text = passwordConfirmError,
                        color = Color.Red,
                    )
                }
            }
        }
        Column(modifier = modifier.padding(5.dp)) {
            SelectableDropdownMenu(names = Constants.GENRES, selectedNames = genres, updateGenres)
            Box(modifier = modifier) {
                if (genresError.isNotEmpty()) {
                    Text(
                        text = genresError,
                        color = Color.Red,
                    )
                }
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = modifier.padding(10.dp)
        ) {
            Checkbox(
                checked = isArtist,
                onCheckedChange = { isArtist = it }
            )
            Text("Artist")
        }
        Box(modifier = Modifier.padding(bottom = 5.dp)) {
            Button(
                onClick = {
                    if (email.isEmpty()) {
                        emailError = "Email is not entered"
                        isCanTryRegister = false
                    } else if (!isValidEmail(email)) {
                        emailError = "Invalid email format"
                        isCanTryRegister = false
                    } else {
                        emailError = ""
                    }
                    if (password.isEmpty()) {
                        passwordError = "Password is not entered"
                        isCanTryRegister = false
                    } else if (!isValidPassword(password)) {
                        passwordError =
                            "Invalid password format. It must be at least 8 characters long, contain lowercase, uppercase letters and a number"
                        isCanTryRegister = false
                    } else {
                        passwordError = ""
                    }
                    if (password != passwordConfirm) {
                        passwordConfirmError = "Passwords don't match"
                        isCanTryRegister = false
                    } else {
                        passwordConfirmError = ""
                    }
                    if(genres.isEmpty()){
                        genresError = "Choose at least one genre"
                        isCanTryRegister = false
                    }
                    else{
                        genresError = ""
                    }
                    if (isCanTryRegister) {
                        Firebase.auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener {
                                if (it.isSuccessful) {
                                    if (isArtist) {
                                        role = "artist"
                                    }
                                    val userinfo = mapOf(
                                        "username" to username,
                                        "email" to email,
                                        "role" to role,
                                        "genres" to genres,
                                        "favouriteTracks" to listOf<String>()
                                    )
                                    db.child("Users")
                                        .child(Firebase.auth.currentUser!!.uid)
                                        .setValue(userinfo)
                                    user = Firebase.auth.currentUser
                                    clearAllActivities(activity!!.applicationContext, Intent(
                                        activity,
                                        MainActivity::class.java
                                    ))
                                }
                                else{
                                    isShowAlertDialog = true
                                }
                            }
                            .addOnFailureListener {
                                isShowAlertDialog = true
                            }
                    }
                },
                modifier = Modifier.fillMaxWidth(0.75f)
            ) {
                Text(text = "Sign up")
            }
        }
        Row(modifier = Modifier.padding(bottom = 10.dp)) {
            Text(
                text = "Already have an account?  ",
            )
            Text(
                text = "Sign in",
                color = Color(122, 215, 250),
                modifier = Modifier.clickable {
                    activity?.startActivity(Intent(activity, LoginActivity::class.java))
                }
            )
        }
        Row {
            Text(
                text = "Home",
                color = Color(122, 215, 250),
                modifier = Modifier.clickable {
                    clearAllActivities(
                        activity!!.applicationContext, Intent(
                            activity,
                            MainActivity::class.java
                        )
                    )
                }
            )
        }
    }
//    }
}





@Preview(widthDp = 700, heightDp = 1300)
@Composable
fun RegistrationPagePreview(){
    RegistrationPage()
}