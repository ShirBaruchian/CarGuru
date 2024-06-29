package com.example.carguru.data.remote

import com.example.carguru.data.model.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.tasks.await
import java.util.Date

class FirebaseUserService {
    private val firestore = FirebaseFirestore.getInstance()

    fun addUserListener(onUsersChanged: (List<User>) -> Unit): ListenerRegistration {
        return firestore.collection("users")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    // Handle error
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    val users = snapshot.toObjects(User::class.java)
                    onUsersChanged(users)
                }
            }
    }

    suspend fun getUser(userId: String): User? {
        val userSnapshot = firestore.collection("users").document(userId).get().await()
        return userSnapshot.toObject(User::class.java)
    }

    suspend fun saveUser(user: User) {
        firestore.collection("users").document(user.id).set(user).await()
    }

    suspend fun getAllUsers(since: Date? = null): List<User> {
        val query = if (since != null) {
            firestore.collection("users").whereGreaterThan("lastUpdated", since)
        } else {
            firestore.collection("users")
        }
        val usersSnapshot = query.get().await()
        return usersSnapshot.documents.mapNotNull { it.toObject(User::class.java) }
    }

    suspend fun updateUser(user: User) {
        firestore.collection("users").document(user.id).set(user).await()
    }

    suspend fun deleteUser(userId: String) {
        firestore.collection("users").document(userId).delete().await()
    }
}
