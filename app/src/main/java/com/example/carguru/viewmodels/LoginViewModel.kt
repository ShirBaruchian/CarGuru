package com.example.carguru.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.carguru.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class LoginViewModel(): ViewModel() {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

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

    fun onLoginClick(callback: (Boolean) -> Unit) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true)
                } else {
                    errorMessage.value = task.exception?.message ?: "Log in failed"
                    callback(false)
                }
            }
    }

    fun signInWithGoogle(idToken: String, callback: (Boolean) -> Unit){
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    if (user != null) {
                        viewModelScope.launch {
                            val userDoc = firestore.collection("users").document(user.uid).get().await()
                            if (!userDoc.exists()) {
                                // If user data does not exist, create a new document with basic details
                                val newUser = User(user.uid,
                                    user.displayName ?: "Unknown",
                                    "",
                                    user.email ?: "Unknown",
                                    ""
                                )
                                firestore.collection("users").document(user.uid).set(newUser).await()
                            }
                            callback(true)
                        }
                    } else {
                        errorMessage.value = "Failed to retrieve user data"
                        callback(false)
                    }
                } else {
                    errorMessage.value = task.exception?.message ?: "Sign-In failed"
                }
            }
    }

    fun onFacebookLoginClick() {
        // Implement Facebook login logic here
    }
}