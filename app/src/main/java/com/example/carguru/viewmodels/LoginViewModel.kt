package com.example.carguru.viewmodels

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.runtime.mutableStateOf
import com.google.firebase.auth.GoogleAuthProvider

class LoginViewModel(): ViewModel() {
    private val firebaseAuth = FirebaseAuth.getInstance()
    var email: String by mutableStateOf("")
        private set

    var password: String by mutableStateOf("")
        private set

    var errorMessage = mutableStateOf<String?>(null)
        private set

    fun onEmailChange(newEmail: String) {
        email = newEmail
    }

    fun onPasswordChange(newPassword: String) {
        password = newPassword
    }

    fun onLoginClick(onSuccess: (String) -> Unit) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess(firebaseAuth.currentUser?.displayName ?: "User")
                } else {
                    errorMessage.value = task.exception?.message ?: "Log in failed"
                }
            }
    }

    fun signInWithGoogle(idToken: String, onSuccess: (String) -> Unit){
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    val displayName = user?.displayName ?: "User"
                    onSuccess(displayName)
                } else {
                    errorMessage.value = task.exception?.message ?: "Sign-In failed"
                }
            }
    }

    fun onFacebookLoginClick() {
        // Implement Facebook login logic here
    }
}