package com.example.carguru.viewmodels

import java.util.Date
import android.net.Uri
import kotlinx.coroutines.launch
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import androidx.compose.runtime.mutableStateOf
import com.example.carguru.data.local.UserEntity
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.auth.UserProfileChangeRequest
import com.example.carguru.data.repository.UserRepository

class SignUpViewModel(private val userRepository: UserRepository) : ViewModel() {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val storageReference: StorageReference = FirebaseStorage.getInstance().reference

    var email = mutableStateOf("")
        private set

    var password = mutableStateOf("")
        private set

    var name = mutableStateOf("")
        private set

    var errorMessage = mutableStateOf<String?>(null)
        private set

    fun onEmailChange(newEmail: String) {
        email.value = newEmail
    }

    fun onPasswordChange(newPassword: String) {
        password.value = newPassword
    }

    fun onNameChange(newName: String) {
        name.value = newName
    }

    fun onSignUpClick(profileImageUri: Uri?,onSuccess: () -> Unit) {
        firebaseAuth.createUserWithEmailAndPassword(email.value, password.value)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = task.result?.user
                    user?.let {
                        if (profileImageUri != null) {
                            uploadProfileImage(profileImageUri, user, onSuccess)
                        } else {
                            updateProfile(user, "", onSuccess)
                        }
                    }
                } else {
                    errorMessage.value = task.exception?.message ?: "Sign-Up failed"
                }
            }
    }

    private fun uploadProfileImage(uri: Uri, user: FirebaseUser, onSuccess: () -> Unit) {
        val profileImageRef = storageReference.child("profileImages/${user.uid}.jpg")
        profileImageRef.putFile(uri)
            .addOnSuccessListener {
                profileImageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                    updateProfile(user, downloadUrl.toString(), onSuccess)
                }.addOnFailureListener { exception ->
                    errorMessage.value = exception.message ?: "Failed to get download URL"
                }
            }
            .addOnFailureListener { exception ->
                errorMessage.value = exception.message ?: "Failed to upload profile image"
            }
    }

    private fun updateProfile(user: FirebaseUser, imageUrl: String?, onSuccess: () -> Unit) {
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(name.value)
            .build()

        user.updateProfile(profileUpdates)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    saveUserDetails(user,imageUrl, onSuccess)
                } else {
                    errorMessage.value = task.exception?.message ?: "Failed to update profile"
                }
            }
    }

    private fun saveUserDetails(user: FirebaseUser, imageUrl: String?, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {

                val newUser = imageUrl?.let {
                    UserEntity(
                        id = user.uid,
                        username = name.value,
                        email = email.value,
                        password = password.value,
                        lastUpdated = Date(),
                        profileImageUrl = it
                    )
                }
                if (newUser != null) {
                    userRepository.saveUser(newUser)
                }
                onSuccess()

            }
            catch (e: Exception) {
                errorMessage.value = e.message ?: "Failed to save user details"
            }
        }
    }
}