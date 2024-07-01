package com.example.carguru.data.remote

import com.example.carguru.data.model.Review
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.tasks.await
import java.util.Date

class FirebaseReviewService {
    private val firestore = FirebaseFirestore.getInstance()

    fun addReviewListener(onReviewsChanged: (List<Review>) -> Unit): ListenerRegistration {
        return firestore.collection("reviews")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    // Handle error
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    val reviews = snapshot.toObjects(Review::class.java)
                    onReviewsChanged(reviews)
                }
            }
    }

    suspend fun getReview(reviewId: String): Review? {
        val reviewSnapshot = firestore.collection("reviews").document(reviewId).get().await()
        return reviewSnapshot.toObject(Review::class.java)
    }

    suspend fun saveReview(review: Review) {
        firestore.collection("reviews").document(review.id).set(review).await()
    }

    suspend fun getAllReviews(since: Date? = null): List<Review> {
        val query = if (since != null) {
            firestore.collection("reviews").whereGreaterThan("lastUpdated", since)
        } else {
            firestore.collection("reviews")
        }
        val reviewsSnapshot = query.get().await()
        return reviewsSnapshot.documents.mapNotNull { it.toObject(Review::class.java) }
    }

    suspend fun getReviewsUpdatedAfter(date: Date): List<Review> {
        val reviews = mutableListOf<Review>()
        val querySnapshot = firestore.collection("reviews")
            .whereGreaterThan("lastUpdated", date)
            .get()
            .await()

        for (document in querySnapshot.documents) {
            document.toObject(Review::class.java)?.let { reviews.add(it) }
        }

        return reviews
    }

    suspend fun updateReview(review: Review) {
        firestore.collection("reviews").document(review.id).set(review).await()
    }

    suspend fun deleteReview(reviewId: String) {
        firestore.collection("reviews").document(reviewId).delete().await()
    }
}
