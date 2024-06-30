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
    private var _loading = MutableStateFlow(false)
    var loading = _loading.asStateFlow()

    val reviews: StateFlow<List<ReviewWithUser>> = reviewRepository.getAllReviewsWithUser()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    init {
        reviewRepository.startListeningForUpdates(viewModelScope)
        viewModelScope.launch {
            try {
                _loading.value = true
                reviewRepository.syncReviews()
                _loading.value = false
            } catch (e: Exception) {
                // Handle the exception
            }
        }
    }

    //fun fetchReviews() {
    //    viewModelScope.launch {
    //        val reviews = reviewRepository.getAllReviews()
    //        val userIds = reviews.map { it.userId }.distinct()
    //        val userMap = userIds.associateWith { userId ->
    //            userRepository.getUser(userId)?.username ?: "Unknown"
    //        }
    //        reviews.value = reviews.map { review ->
    //            ReviewWithUser(review, userMap[review.userId] ?: "Unknown")
    //        }
    //    }
    //}

    fun getReview(reviewId: String): StateFlow<ReviewWithUser?> {
        return reviewRepository.getReview(reviewId)
            .stateIn(viewModelScope, SharingStarted.Lazily, null)
    }
}
