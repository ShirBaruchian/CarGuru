package com.example.carguru.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.carguru.data.repository.ReviewRepository
import com.example.carguru.data.repository.UserRepository
import com.example.carguru.models.Review
import com.example.carguru.models.ReviewWithUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ReviewsViewModel(private val reviewRepository: ReviewRepository,
                       private val userRepository: UserRepository
) : ViewModel() {
    private val _reviews = MutableStateFlow<List<ReviewWithUser>>(emptyList())
    val reviews: StateFlow<List<ReviewWithUser>> = _reviews

    init {
        reviewRepository.startListeningForUpdates(viewModelScope)
        viewModelScope.launch {
            try {
                reviewRepository.syncReviews()
            } catch (e: Exception) {
                // Handle the exception
            }
        }
    }

    fun fetchReviews() {
        viewModelScope.launch {
            val reviews = reviewRepository.getAllReviews()
            val userIds = reviews.map { it.userId }.distinct()
            val userMap = userIds.associateWith { userId ->
                userRepository.getUser(userId)?.username ?: "Unknown"
            }
            _reviews.value = reviews.map { review ->
                ReviewWithUser(review, userMap[review.userId] ?: "Unknown")
            }
        }
    }

    fun getReviewWithUser(reviewId: String): StateFlow<ReviewWithUser?> {
        val reviewWithUser = MutableStateFlow<ReviewWithUser?>(null)
        viewModelScope.launch {
            val review = reviewRepository.getReview(reviewId)
            if (review != null) {
                val user = userRepository.getUser(review.userId)
                reviewWithUser.value = ReviewWithUser(review, user?.username ?: "Unknown")
            }
        }
        return reviewWithUser
    }
}
