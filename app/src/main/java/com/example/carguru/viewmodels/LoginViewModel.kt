package com.example.carguru.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LoginViewModel(): ViewModel() {
    private val firebaseAuth = FirebaseAuth.getInstance()

    var email: String by mutableStateOf("")
        private set

    var password: String by mutableStateOf("")
        private set

    fun onEmailChange(newEmail: String) {
        email = newEmail
    }

    fun onPasswordChange(newPassword: String) {
        password = newPassword
    }

    fun onLoginClick() {
        // Implement login logic here
    }

    fun signInWithGoogle(idToken: String) {

    }

    fun onFacebookLoginClick() {
        // Implement Facebook login logic here
    }
}