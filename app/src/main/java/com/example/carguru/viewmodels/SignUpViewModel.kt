package com.example.carguru.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore

class SignUpViewModel : ViewModel() {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    var email: String by mutableStateOf("")
        private set

    var password: String by mutableStateOf("")
        private set

    var name: String by mutableStateOf("")
        private set

    var errorMessage = mutableStateOf<String?>(null)
        private set

    fun onEmailChange(newEmail: String) {
        email = newEmail
    }

    fun onPasswordChange(newPassword: String) {
        password = newPassword
    }

    fun onNameChange(newName: String) {
        name = newName
    }

    fun onSignUpClick(onSuccess: () -> Unit) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = task.result?.user
                    user?.let { updateProfile(it, onSuccess) }
                } else {
                    errorMessage.value = task.exception?.message ?: "Sign-Up failed"
                }
            }
    }

    private fun updateProfile(user: FirebaseUser, onSuccess: () -> Unit) {
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(name)
            .build()

        user.updateProfile(profileUpdates)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    saveUserDetails(user, onSuccess)
                } else {
                    errorMessage.value = task.exception?.message ?: "Failed to update profile"
                }
            }
    }

    private fun saveUserDetails(user: FirebaseUser, onSuccess: () -> Unit) {
        val userDetails = hashMapOf(
            "uid" to user.uid,
            "name" to name,
            "email" to email,
            "password" to password
        )
        firestore.collection("users").document(user.uid)
            .set(userDetails)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> errorMessage.value = e.message ?: "Failed to save user details" }
    }
}
