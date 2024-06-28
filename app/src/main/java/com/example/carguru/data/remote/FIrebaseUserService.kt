package com.example.carguru.data.remote

import com.example.carguru.models.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirebaseUserService {
    private val firestore = FirebaseFirestore.getInstance()

    suspend fun syncUserWithFirestore(user: User) {
        firestore.collection("users").document(user.id).set(user).await()
    }

    suspend fun fetchUserFromFirestore(userId: String): User? {
        val userSnapshot = firestore.collection("users").document(userId).get().await()
        return userSnapshot.toObject(User::class.java)
    }

    suspend fun fetchUsersFromFirestore(): List<User> {
        val usersSnapshot = firestore.collection("users").get().await()
        return usersSnapshot.documents.mapNotNull { it.toObject(User::class.java) }
    }
}
