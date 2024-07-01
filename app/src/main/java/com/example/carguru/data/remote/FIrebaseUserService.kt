package com.example.carguru.data.remote

import com.example.carguru.data.model.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.tasks.await
import java.util.Date

class FirebaseUserService {
    private val firestore = FirebaseFirestore.getInstance()

    fun addUserListener(onUserChange: (List<User>) -> Unit) {
        firestore.collection("users")
            .addSnapshotListener { snapshot, e ->
                if (e != null || snapshot == null) {
                    return@addSnapshotListener
                }

                val users = snapshot.documents.mapNotNull { it.toObject(User::class.java) }
                onUserChange(users)
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
        print("Updating user")
        firestore.collection("users").document(user.id).set(user).await()
    }

    suspend fun deleteUser(userId: String) {
        firestore.collection("users").document(userId).delete().await()
    }

    suspend fun getUsersUpdatedAfter(date: Date): List<User> {
        val users = mutableListOf<User>()
        val querySnapshot = firestore.collection("users")
            .whereGreaterThan("lastUpdated", date)
            .get()
            .await()

        for (document in querySnapshot.documents) {
            document.toObject(User::class.java)?.let { users.add(it) }
        }

        return users
    }
}
