package com.example.carguru.data.remote

import com.example.carguru.models.Review
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirebaseReviewService {
    private val firestore = FirebaseFirestore.getInstance()

    suspend fun syncReviewWithFirestore(review: Review) {
        firestore.collection("reviews").document(review.id).set(review).await()
    }

    suspend fun fetchReviewsFromFirestore(): List<Review> {
        val reviewsSnapshot = firestore.collection("reviews").get().await()
        return reviewsSnapshot.documents.mapNotNull { it.toObject(Review::class.java) }
    }
}
