package com.example.carguru.viewmodels

import java.util.Date
import java.util.Locale
import java.util.Calendar
import java.text.SimpleDateFormat
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import com.example.carguru.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.example.carguru.data.local.UserEntity
import com.example.carguru.data.repository.UserRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.launch

class SignUpViewModel(private val userRepository: UserRepository) : ViewModel() {
    private val firebaseAuth = FirebaseAuth.getInstance()

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

    private fun isValidAge(birthdate: String): Boolean {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.US)
        val birthDate: Date = sdf.parse(birthdate) ?: return false
        val today = Calendar.getInstance()
        val birthDay = Calendar.getInstance()
        birthDay.time = birthDate

        var age = today.get(Calendar.YEAR) - birthDay.get(Calendar.YEAR)
        if (today.get(Calendar.DAY_OF_YEAR) < birthDay.get(Calendar.DAY_OF_YEAR)) {
            age--
        }

        return age >= 18
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
        viewModelScope.launch {
            try {

                val newUser = UserEntity(
                    id = user.uid,
                    username = name,
                    email = email,
                    password = password,
                    lastUpdated = Date()
                )
                userRepository.saveUser(newUser)
                onSuccess()

            }
            catch (e: Exception) {
                errorMessage.value = e.message ?: "Failed to save user details"
            }
        }
    }
}
