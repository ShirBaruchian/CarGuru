package com.example.carguru.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.carguru.models.Review
import com.example.carguru.models.ReviewWithUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ReviewsViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()

    private val _reviews = MutableStateFlow<List<ReviewWithUser>>(emptyList())
    val reviews: StateFlow<List<ReviewWithUser>> = _reviews

    init {
        fetchReviews()
    }

    fun fetchReviews() {
        viewModelScope.launch {
            try {
                val query: Query = firestore.collection("reviews").orderBy("timestamp", Query.Direction.DESCENDING)

                val reviewsSnapshot = query.get().await()
                val reviews = if (reviewsSnapshot.isEmpty) {
                    emptyList()
                } else {
                    reviewsSnapshot.map { it.toObject(Review::class.java) }
                }

                val userIds = reviews.map { it.userId }.distinct()
                val userMap = mutableMapOf<String, String>()

                for (userId in userIds) {
                    val userSnapshot = firestore.collection("users").document(userId).get().await()
                    val userName = userSnapshot.getString("name") ?: "Unknown"
                    userMap[userId] = userName
                }

                val reviewsWithUserNames = reviews.map { review ->
                    ReviewWithUser(review, userMap[review.userId] ?: "Unknown")
                }

                _reviews.value = reviewsWithUserNames
            } catch (e: Exception) {
                // Handle the error, for example log it or update a separate error state
                _reviews.value = emptyList()
            }
        }
    }
}
