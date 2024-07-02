package com.example.carguru.viewmodels

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.example.carguru.data.local.UserEntity
import com.example.carguru.data.repository.UserRepository
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Date

class LoginViewModel(private val userRepository: UserRepository): ViewModel() {
    private val firebaseAuth = FirebaseAuth.getInstance()

    var email = mutableStateOf("")
        private set

    var password = mutableStateOf("")
        private set

    var errorMessage = mutableStateOf<String?>(null)
        private set

    fun onEmailChange(newEmail: String) {
        email.value = newEmail
    }

    fun onPasswordChange(newPassword: String) {
        password.value = newPassword
    }

    fun onLoginClick(onSuccess: (Boolean) -> Unit) {
        firebaseAuth.signInWithEmailAndPassword(email.value, password.value)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess(true)
                } else {
                    errorMessage.value = task.exception?.message ?: "Log in failed"
                }
            }
    }

    fun signInWithGoogle(idToken: String, callback: (Boolean) -> Unit) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    if (user != null) {
                        viewModelScope.launch {
                            val userDoc = userRepository.getUser(user.uid)
                            if (userDoc == null) {
                                // If user data does not exist, create a new document with basic details
                                val newUser = UserEntity(user.uid,
                                    user.displayName ?: "Unknown",
                                    "",
                                    user.email ?: "Unknown",
                                    Date(),
                                    ""
                                )
                                userRepository.saveUser(newUser)
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
}
