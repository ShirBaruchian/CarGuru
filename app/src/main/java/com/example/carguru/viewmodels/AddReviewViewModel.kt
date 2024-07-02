package com.example.carguru.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.carguru.data.local.ReviewEntity
import com.example.carguru.data.repository.ReviewRepository
import com.example.carguru.models.Review
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Date
import java.util.UUID

class AddReviewViewModel(private val reviewRepository: ReviewRepository) : ViewModel() {
    private val auth = FirebaseAuth.getInstance()

    fun addReview(
        title: String, manufacturer: String, model: String, year: String, trim: String, rating: Int, reviewText: String, callback: (String?) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val userId = auth.currentUser?.uid
                if (userId != null) {
                    val newReview = ReviewEntity(
                        title = title,
                        manufacturer = manufacturer,
                        model = model,
                        year = year,
                        trim = trim,
                        rating = rating,
                        text = reviewText,
                        userId = userId,
                        timestamp = Date(),
                        lastUpdated = Date()
                    )
                    reviewRepository.saveReview(newReview)
                    callback(null)
                }
            } catch (e: Exception) {
                callback(e.message)
            }
        }
    }
}
