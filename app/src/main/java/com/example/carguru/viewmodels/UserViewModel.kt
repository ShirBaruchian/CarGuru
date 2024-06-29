package com.example.carguru.viewmodels
import androidx.lifecycle.ViewModel
import com.example.carguru.data.model.User
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.runtime.mutableStateOf
import com.google.firebase.firestore.FirebaseFirestore

class UserViewModel : ViewModel() {
    var user = mutableStateOf<User?>(null)
        private set

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    init {
        fetchUserDetails()
    }

    private fun setUser(newUser: User) {
        user.value = newUser
    }

    fun fetchUserDetails() {
        val currentUser = firebaseAuth.currentUser
        currentUser?.let { user ->
            firestore.collection("users").document(user.uid).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val username = document.getString("username") ?: ""
                        val password = document.getString("password") ?: ""
                        val birthdate = document.getString("birthdate") ?: ""
                        val userDetails = User(
                            id = user.uid,
                            username = username,
                            password = password,
                            email = user.email ?: "",
                            birthdate = birthdate
                        )
                        userDetails.let {
                            setUser(it)
                        }
                    }
                }
        }
    }
    fun updateUserDetails(userId: String, newUsername: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        val userUpdates = mapOf(
            "username" to newUsername,
        )

        firestore.collection("users").document(userId).update(userUpdates)
            .addOnSuccessListener {
                user.value = user.value?.copy(username = newUsername)
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
