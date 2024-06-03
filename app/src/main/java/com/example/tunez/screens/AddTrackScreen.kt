package com.example.tunez.screens

import androidx.compose.runtime.Composable
import com.google.firebase.Firebase
import com.google.firebase.storage.storage

@Composable
fun AddTrackScreen(){
    Firebase.storage.reference.child("images")
}