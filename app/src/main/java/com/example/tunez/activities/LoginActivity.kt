package com.example.tunez.activities

import android.app.Activity
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.adamratzman.spotify.auth.implicit.startSpotifyImplicitLoginActivity
import com.example.tunez.R
import com.example.tunez.auth.SpotifyImplicitLoginActivityImpl
import com.example.tunez.auth.SpotifyPkceLoginActivityImpl
import com.example.tunez.ui.theme.TunezTheme
import com.example.tunez.utils.toast
import com.example.tunez.viewmodels.ProfileViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.runBlocking
import org.koin.androidx.compose.inject

class LoginActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TunezTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    LoginPage(activity = this)
                }
            }
        }
        // Переход на главную страницу при нажатии кнопки назад
        val callback = onBackPressedDispatcher.addCallback(this) {
            clearAllActivities(applicationContext, Intent(
                applicationContext,
                MainActivity::class.java
            ))
        }
    }
}


@Composable
fun LoginPage(modifier: Modifier = Modifier, activity: Activity? = null){
    val vm: ProfileViewModel by inject()
    var email by remember{ mutableStateOf("") }
    var password by remember{ mutableStateOf("") }
    var emailError by remember{ mutableStateOf("") }
    var passwordError by remember{ mutableStateOf("") }
    var isCanTryAuthenticate by remember{ mutableStateOf(true) }
    var isShowAlertDialog by remember{ mutableStateOf(false) }
    if(isShowAlertDialog){
        ErrorAlert(
            title = "Not found",
            message = "A user with such credentials was not found. Try again",
            onDismissRequest = { isShowAlertDialog = false },
            onConfirm = { isShowAlertDialog = false }
        )
    }
    Row() {
        Text(
            text = "Authorization",
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            modifier = Modifier
                .padding(0.dp, 20.dp)
                .fillMaxWidth()
        )
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center) {
        Column(modifier = Modifier.padding(0.dp, 10.dp)) {
            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                leadingIcon = { Icon(imageVector = Icons.Default.Email, contentDescription = "") },
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth(0.8f)
            )
            if(emailError.isNotEmpty()) {
                Text(
                    text = emailError,
                    color = Color.Red
                )
            }
        }
        Column(modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 20.dp)) {
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
                    .fillMaxWidth(0.8f)
            )
            if(passwordError.isNotEmpty()){
                Text(
                    text = passwordError,
                    color = Color.Red
                )
            }
        }
        Box(modifier = Modifier.padding(bottom = 5.dp)) {
            Button(
                onClick = {
                    if (email.isEmpty()){
                        emailError = "Email is not entered"
                        isCanTryAuthenticate = false
                    }
                    else if(!isValidEmail(email)){
                        emailError = "Invalid email format"
                        isCanTryAuthenticate = false
                    }
                    else{
                        emailError = ""
                    }
                    if (password.isEmpty()){
                        passwordError = "Password is not entered"
                        isCanTryAuthenticate = false
                    }
                    else{
                        passwordError = ""
                    }
                    if(isCanTryAuthenticate){
                        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener {
                                if (it.isSuccessful) {
                                    user = Firebase.auth.currentUser
                                    clearAllActivities(activity!!.applicationContext, Intent(
                                        activity,
                                        MainActivity::class.java
                                    ))
                                    vm.getAllInfo()
                                }
                            }
                            .addOnFailureListener {
                                isShowAlertDialog = true
                            }
                    }
                    isCanTryAuthenticate = true

                },
                modifier = Modifier.fillMaxWidth(0.75f)
            ) {
                Text(text = "Sign in")
            }
        }
        Row(modifier = Modifier.padding(bottom = 10.dp)) {
            Text(
                text = "Don't you have an account?  ",
            )
            Text(
                text = "Sign up",
                color = Color(122,215,250),
                modifier = Modifier.clickable {
                    activity?.startActivity(Intent(activity, RegistrationActivity::class.java))
                }
            )
        }
        Row {
            Text(
                text = "Home",
                color = Color(122,215,250),
                modifier = Modifier.clickable {
                    clearAllActivities(activity!!.applicationContext, Intent(
                        activity,
                        MainActivity::class.java
                    ))
                }
            )
        }
    }
}

@Composable
fun ErrorAlert(
    title: String,
    message: String,
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit
){
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(title)
        },
        text = {
            Text(message)
        },
        confirmButton = {
            Button(
                onClick = onConfirm
            ) {
                Text("Confirm")
            }
        },
    )
}

fun clearAllActivities(context: Context, newIntent: Intent) {
    val taskStackBuilder = TaskStackBuilder.create(context)
    taskStackBuilder.addNextIntentWithParentStack(newIntent)
    taskStackBuilder.startActivities()
}
fun isValidPassword(password: String): Boolean {
    val passwordRegex = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d_]{8,}$")
    return password.matches(passwordRegex)
}

fun isValidEmail(email: String): Boolean {
    val emailRegex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$")
    return email.matches(emailRegex)
}
@Preview(widthDp = 700, heightDp = 1300)
@Composable
fun LoginPagePreview(){
    LoginPage()
}