package com.example.carguru.viewmodels
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.carguru.data.model.User
import com.google.firebase.auth.FirebaseAuth
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class UserViewModel : ViewModel() {
    private val _userName = MutableStateFlow("")
    val userName: StateFlow<String> = _userName

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    init {
        fetchUserDetails()
    }

    fun fetchUserDetails() {
        viewModelScope.launch {
            try {
                val userId = firebaseAuth.currentUser?.uid
                if (userId != null) {
                    Log.d("UserViewModel", "Fetching user data for userId: $userId")
                    val userSnapshot = firestore.collection("users").document(userId).get().await()
                    val user = userSnapshot.toObject(User::class.java)
                    if (user != null) {
                        _userName.value = user.username
                        _user.value = user
                    } else {
                        _userName.value = "Unknown"
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
        val userUpdates = mapOf(
            "username" to newUsername
        )

        firestore.collection("users").document(userId).update(userUpdates)
            .addOnSuccessListener {
                _user.value = _user.value?.copy(username = newUsername)
                _userName.value = newUsername
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception.message ?: "Failed to update user details.")
            }
    }

    fun logout() {
        firebaseAuth.signOut()
    }
}
