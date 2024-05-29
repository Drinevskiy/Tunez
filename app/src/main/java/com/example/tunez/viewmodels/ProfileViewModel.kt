package com.example.tunez.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.tunez.activities.MainActivity
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import com.example.tunez.activities.user
import com.google.firebase.Firebase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import com.google.firebase.database.getValue
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ProfileViewModel: ViewModel() {
    private var _uiState = MutableStateFlow(ProfileUiState())
    val profileUiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        getAllInfo()
    }
    fun updateUiState(uiState: ProfileUiState){
        _uiState.update {
            it.copy(
                user = uiState.user,
                uid = uiState.uid,
                username = uiState.username,
                role = uiState.role,
                email = uiState.email,
                genres = uiState.genres
            )
        }
    }
    fun getAllInfo(){
        getUid()
        getRole()
        getUsername()
        getEmail()
        getGenres()
    }
    fun getUid(){
        if(user != null) {
            val uid = Firebase.database.reference.child("Users").child(profileUiState.value.user!!.uid)
            updateUiState(profileUiState.value.copy(uid = uid))
        }
    }
    fun getRole(){
        if(user != null) {
            profileUiState.value.uid?.child("role")?.get()?.addOnSuccessListener {
                val role = it.value.toString()
                updateUiState(profileUiState.value.copy(role = role))
            }?.addOnFailureListener {
                Log.e("firebase", "Error getting data", it)
            }
        }
    }
    fun getUsername(){
        if(user != null) {
            profileUiState.value.uid?.child("username")?.get()?.addOnSuccessListener {
                val username = it.value.toString()
                updateUiState(profileUiState.value.copy(username = username))
            }?.addOnFailureListener {
                Log.e("firebase", "Error getting data", it)
            }
        }
    }
    fun getEmail(){
        if(user != null) {
            profileUiState.value.uid?.child("email")?.get()?.addOnSuccessListener {
                val email = it.value.toString()
                updateUiState(profileUiState.value.copy(email = email))
            }?.addOnFailureListener {
                Log.e("firebase", "Error getting data", it)
            }
        }
    }
    fun getGenres(){
        if(user != null) {
            profileUiState.value.uid?.child("genres")?.get()?.addOnSuccessListener {
                val genres = it.value as List<String>
                updateUiState(profileUiState.value.copy(genres = genres))
            }?.addOnFailureListener {
                Log.e("firebase", "Error getting data", it)
            }
        }
    }
    fun getAllUsers(){
        if(profileUiState.value.role == "admin"){
            Firebase.database.reference.child("Users").get()
                .addOnSuccessListener { dataSnapshot ->
                    if (dataSnapshot.exists()) {
                        // Iterate through the children (users) and get their values
                        for (childSnapshot in dataSnapshot.children) {
                            val user = childSnapshot.getValue()
                            // Do something with the user data
//                            user.
                            Log.i("firebase", user.toString())
                        }
                    } else {
                        // The "Users" node is empty
                        println("No users found.")
                    }
                }
                .addOnFailureListener { error ->
                    Log.e("firebase", "Node not found")
                }
        }
    }
}

data class ProfileUiState(
    val user: FirebaseUser? = com.example.tunez.activities.user,
    val username: String = "",
    val role: String = "",
    val email: String = "",
    val uid: DatabaseReference? = null,
    val genres: List<String> = listOf()
)