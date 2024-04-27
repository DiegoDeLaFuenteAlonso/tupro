package com.diego.tupro.navigation

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class UserViewModel : ViewModel() {
    private val auth = Firebase.auth

    // LiveData que representa el estado de la sesión del usuario
    val userSession: LiveData<FirebaseUser?> = liveData {
        emit(auth.currentUser)
    }
}

