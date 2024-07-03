package com.example.carguru.data.remote

import java.util.Date
import kotlinx.coroutines.tasks.await
import com.example.carguru.data.model.User
import com.google.firebase.firestore.FirebaseFirestore

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

    suspend fun updateUser(user: User) {
        print("Updating user")
        firestore.collection("users").document(user.id).set(user).await()
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
