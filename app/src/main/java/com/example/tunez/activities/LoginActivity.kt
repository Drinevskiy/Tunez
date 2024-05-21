package com.example.tunez.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
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
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.tunez.auth.SpotifyPkceLoginActivityImpl
import com.example.tunez.ui.theme.TunezTheme
import com.example.tunez.utils.toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.runBlocking

class LoginActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TunezTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    LoginPage(this)
                }
            }
        }
    }
}

@Composable
fun LoginPage(activity: Activity, modifier: Modifier = Modifier){
    var email by remember{ mutableStateOf("") }
    var password by remember{ mutableStateOf("") }
    Column {
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
        Row(modifier = modifier.padding(10.dp)) {
            Button(
                onClick={
                    if(email.isEmpty()||password.isEmpty()){
                        activity.toast("Введены некорректные данные")
                    }
                    else{
                        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener {
                                if(it.isSuccessful){
                                    activity.startActivity(Intent(activity, MainActivity::class.java))
                                }
                            }
                    }
                }
            ){
                Text(text="Sign in")
            }
            Button(
                onClick={
                    activity.startActivity(Intent(activity, RegistrationActivity::class.java))
                }
            ){
                Text(text="Sign up")
            }
        }

    }
}