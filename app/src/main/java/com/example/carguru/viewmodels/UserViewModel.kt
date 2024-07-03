package com.example.carguru.viewmodels

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.carguru.data.model.User
import com.google.firebase.auth.FirebaseAuth
import androidx.lifecycle.viewModelScope
import com.example.carguru.data.local.UserEntity
import com.example.carguru.data.repository.UserRepository
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Date

class UserViewModel(private val userRepository: UserRepository) : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val storageReference: StorageReference = FirebaseStorage.getInstance().reference

    private val _userName = MutableStateFlow("Unknown")

    val userName: StateFlow<String> = _userName

    private val _user = MutableStateFlow<UserEntity?>(null)

    val user: StateFlow<UserEntity?> = _user

    private val _profileImageUrl = MutableStateFlow("")

    val profileImageUrl: StateFlow<String> = _profileImageUrl


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
                            _profileImageUrl.value = user.profileImageUrl
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
    fun updateUserDetails(userId: String, profileImageUrl: String, newUsername: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val user = userRepository.getUser(userId).first()
                if (user != null) {
                    val updatedUser = user.copy(username = newUsername, lastUpdated = Date(), profileImageUrl = profileImageUrl)
                    userRepository.saveUser(updatedUser)
                    _user.value = updatedUser
                    _userName.value = newUsername
                    _profileImageUrl.value = profileImageUrl
                    onSuccess()
                } else {
                    onFailure("User not found")
                }
            } catch (e: Exception) {
                onFailure(e.message ?: "Failed to update user details.")
            }
        }
    }

    private fun updateProfileImage(uri: Uri, userId: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val profileImageRef = storageReference.child("profileImages/${userId}.jpg")
        profileImageRef.putFile(uri)
            .addOnSuccessListener {
                profileImageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                    updateUserDetails(userId, downloadUrl.toString(), userName.value, onSuccess, onFailure)
                }.addOnFailureListener {
                    onFailure("Failed to get download URL")
                }
            }
            .addOnFailureListener {
                onFailure("Failed to upload profile image")
            }
    }

    fun updateProfile(userId: String, newUsername: String, newProfileImageUri: Uri?, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val currentUser = firebaseAuth.currentUser
        currentUser?.let {
            if (newProfileImageUri != null) {
                updateProfileImage(newProfileImageUri, userId, onSuccess, onFailure)
            } else {
                updateUserDetails(userId, profileImageUrl.value, newUsername, onSuccess, onFailure)
            }
        }
    }

    fun logout() {
        firebaseAuth.signOut()
    }
}
