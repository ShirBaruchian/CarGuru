package com.example.carguru.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.withTimeoutOrNull

class UserViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _userName = MutableStateFlow("")
    val userName: StateFlow<String> = _userName

    init {
        fetchCurrentUser()
    }

    fun fetchCurrentUser() {
        viewModelScope.launch {
            try {
                val userId = auth.currentUser?.uid
                if (userId != null) {
                    Log.d("UserViewModel", "Fetching user data for userId: $userId")

                    val userSnapshot = withTimeoutOrNull(5000) {
                        firestore.collection("users").document(userId).get().await()
                    }

                    if (userSnapshot != null && userSnapshot.exists()) {
                        val userName = userSnapshot.getString("username") ?: "Unknown"
                        Log.d("UserViewModel", "Fetched username: $userName")
                        _userName.value = userName
                    } else {
                        Log.d("UserViewModel", "User document does not exist or timed out")
                        _userName.value = "Unknown"
                    }
                } else {
                    Log.d("UserViewModel", "User ID is null")
                    _userName.value = "Unknown"
                }
            } catch (e: Exception) {
                Log.e("UserViewModel", "Failed to fetch user data", e)
                _userName.value = "Unknown"
            }
        }
    }

    fun logout() {
        auth.signOut()
    }
}
