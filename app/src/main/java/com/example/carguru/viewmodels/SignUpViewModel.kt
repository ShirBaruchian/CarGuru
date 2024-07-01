package com.example.carguru.viewmodels

import java.util.Date
import android.net.Uri
import android.util.Log
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
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.UserProfileChangeRequest

class SignUpViewModel : ViewModel() {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val storageReference: StorageReference = FirebaseStorage.getInstance().reference

    var email = mutableStateOf("")
        private set

    var password = mutableStateOf("")
        private set

    var name = mutableStateOf("")
        private set

    var birthdate = mutableStateOf("")
        private set

    var errorMessage = mutableStateOf<String?>(null)
        private set

    fun onEmailChange(newEmail: String) {
        email.value = newEmail
    }

    fun onPasswordChange(newPassword: String) {
        password.value = newPassword
    }

    fun onBirthdateChange(newBirthdate: String) {
        birthdate.value = newBirthdate
    }

    fun onNameChange(newName: String) {
        name.value = newName
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

    fun onSignUpClick(profileImageUri: Uri?, onSuccess: () -> Unit) {
        if (!isValidAge(birthdate.value)) {
            errorMessage.value = "You must be at least 18 years old to sign up."
            return
        }

        firebaseAuth.createUserWithEmailAndPassword(email.value, password.value)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = task.result?.user
                    user?.let {
                        if (profileImageUri != null) {
                            uploadProfileImage(profileImageUri, user, onSuccess)
                        } else {
                            updateProfile(user, null, onSuccess)
                        }
                    }
                } else {
                    errorMessage.value = task.exception?.message ?: "Sign-Up failed"
                }
            }
    }

    private fun uploadProfileImage(uri: Uri, user: FirebaseUser, onSuccess: () -> Unit) {
        val profileImageRef = storageReference.child("profileImages/${user.uid}.jpg")
        Log.d("SignUpViewModel", "Uploading image to: ${profileImageRef.path}")
        profileImageRef.putFile(uri)
            .addOnSuccessListener {
                Log.d("SignUpViewModel", "Image upload successful")
                profileImageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                    updateProfile(user, downloadUrl.toString(), onSuccess)
                }.addOnFailureListener { exception ->
                    Log.e("SignUpViewModel", "Failed to get download URL", exception)
                    errorMessage.value = exception.message ?: "Failed to get download URL"
                }
            }
            .addOnFailureListener { exception ->
                Log.e("SignUpViewModel", "Image upload failed", exception)
                errorMessage.value = exception.message ?: "Failed to upload profile image"
            }
    }

    private fun updateProfile(user: FirebaseUser, imageUrl: String?, onSuccess: () -> Unit) {
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(name.value)
            .setPhotoUri(imageUrl?.let { Uri.parse(it) })
            .build()

        user.updateProfile(profileUpdates)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    saveUserDetails(user, imageUrl, onSuccess)
                } else {
                    errorMessage.value = task.exception?.message ?: "Failed to update profile"
                }
            }
    }

    private fun saveUserDetails(user: FirebaseUser, imageUrl: String?, onSuccess: () -> Unit) {
        val userDetails = User(
            id = user.uid,
            username = name.value,
            password = password.value,
            email = email.value,
            birthdate = birthdate.value,
            profileImageUrl = imageUrl
        )
        firestore.collection("users").document(user.uid)
            .set(userDetails)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess()
                } else {
                    errorMessage.value = task.exception?.message ?: "Failed to save user details"
                }
            }
    }
}
