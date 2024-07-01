package com.example.carguru.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.carguru.data.model.User
import com.google.firebase.auth.FirebaseAuth
import androidx.lifecycle.viewModelScope
import com.example.carguru.data.local.UserEntity
import com.example.carguru.data.repository.UserRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Date

class UserViewModel(private val userRepository: UserRepository) : ViewModel() {
    private val firebaseAuth = FirebaseAuth.getInstance()

    private val _userName = MutableStateFlow("Unknown")
    val userName: StateFlow<String> = _userName

    private val _user = MutableStateFlow<UserEntity?>(null)
    val user: StateFlow<UserEntity?> = _user

    private val firestore = FirebaseFirestore.getInstance()

    init {
        userRepository.startListeningForUpdates()
        viewModelScope.launch {
            try {

                userRepository.syncUsers(viewModelScope)
            } catch (e: Exception) {
                Log.e("UserViewModel", "Failed to sync user data", e)
            }
        }
    }

    fun fetchCurrentUser() {
        viewModelScope.launch {
            try {
                val userId = firebaseAuth.currentUser?.uid
                if (userId != null) {
                    userRepository.getUser(userId).collect { user ->
                        if (user != null) {
                            _userName.value = user.username
                            _user.value = user
                        } else {
                            _userName.value = "Unknown"
                        }
                    }
                } else {
                    _userName.value = "Unknown"
                }
            } catch (e: Exception) {
                Log.e("UserViewModel", "Failed to fetch user data", e)
                _userName.value = "Unknown"
            }
        }
    }
    fun updateUserDetails(userId: String, newUsername: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val user = userRepository.getUser(userId).first()
                if (user != null) {
                    val updatedUser = user.copy(username = newUsername, lastUpdated = Date())
                    userRepository.saveUser(updatedUser)
                    _user.value = updatedUser
                    _userName.value = newUsername
                    onSuccess()
                } else {
                    onFailure("User not found")
                }
            } catch (e: Exception) {
                onFailure(e.message ?: "Failed to update user details.")
            }
        }
    }

    fun logout() {
        firebaseAuth.signOut()
    }
}
