package com.example.carguru.viewmodels

import android.net.Uri
import android.util.Log
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.tasks.await
import androidx.lifecycle.viewModelScope
import com.example.carguru.data.model.ReviewWithUser
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.firestore.FirebaseFirestore
import com.example.carguru.data.repository.ReviewRepository

class ReviewsViewModel(private val reviewRepository: ReviewRepository
) : ViewModel() {
    private var _loading = MutableStateFlow(false)
    var loading = _loading.asStateFlow()

    private val _reviewWithUser = MutableStateFlow<ReviewWithUser?>(null)
    val reviewWithUser: StateFlow<ReviewWithUser?> = _reviewWithUser

    private val firestore = FirebaseFirestore.getInstance()
    private val storageReference = FirebaseStorage.getInstance().reference

    private var _reviews = MutableStateFlow<List<ReviewWithUser>>(emptyList())
    private var _filteredReviews = MutableStateFlow<List<ReviewWithUser>>(emptyList())
    var reviews: StateFlow<List<ReviewWithUser>> = _filteredReviews.asStateFlow()

    private val _userReviews = MutableStateFlow<List<ReviewWithUser>>(emptyList())
    val userReviews: StateFlow<List<ReviewWithUser>> = _userReviews.asStateFlow()

    // Filter criteria
    private val _selectedYear = MutableStateFlow<String?>(null)
    private val _selectedMake = MutableStateFlow<String?>(null)
    private val _selectedModel = MutableStateFlow<String?>(null)
    private val _selectedTrim = MutableStateFlow<String?>(null)

    init {
        fetchReviews()
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
        combine(
            _reviews,
            _selectedYear,
            _selectedMake,
            _selectedModel,
            _selectedTrim
        ) { reviews, year, make, model, trim ->
            reviews.filter { review ->
                val matchesYear = year?.let { it.isEmpty() || it == review.review.year } ?: true
                val matchesMake = make?.let { it.isEmpty() || it == review.review.manufacturer } ?: true
                val matchesModel = model?.let { it.isEmpty() || it == review.review.model } ?: true
                val matchesTrim = trim?.let { it.isEmpty() || it == review.review.trim } ?: true
                matchesYear && matchesMake && matchesModel && matchesTrim
            }
        }.onEach { filteredReviews ->
            _filteredReviews.value = filteredReviews
        }.launchIn(viewModelScope)
    }

    fun fetchReview(reviewId: String) {
        viewModelScope.launch {
            reviewRepository.getReview(reviewId).collect { review ->
                _reviewWithUser.value = review
            }
        }
    }

    fun fetchReviews() {
        viewModelScope.launch {
            _loading.value = true
            Log.d("ReviewsViewModel", "Fetching reviews")
            reviewRepository.getAllReviewsWithUser().collect { reviewList ->
                _reviews.value = reviewList
                _loading.value = false
            }
        }
    }

    fun setYear(year: String?) {
        _selectedYear.value = year
    }

    fun setMake(make: String?) {
        _selectedMake.value = make
    }

    fun setModel(model: String?) {
        _selectedModel.value = model
    }

    fun setTrim(trim: String?) {
        _selectedTrim.value = trim
    }

    fun fetchUserReviews(userId: String) {
        viewModelScope.launch {
            reviewRepository.getReviewsByUserId(userId).collect { userReviewsList ->
                _userReviews.value = userReviewsList
            }
        }
    }

    fun updateReview(
        reviewId: String,
        newTitle: String,
        newText: String,
        newImageUri: Uri?,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val reviewRef = firestore.collection("reviews").document(reviewId)
                val reviewSnapshot = reviewRef.get().await()
                if (reviewSnapshot.exists()) {
                    val updates = mutableMapOf<String, Any>(
                        "title" to newTitle,
                        "text" to newText
                    )
                    newImageUri?.let { uri ->
                        val imageRef = storageReference.child("reviewImages/$reviewId.jpg")
                        imageRef.putFile(uri).await()
                        val downloadUrl = imageRef.downloadUrl.await().toString()
                        updates["imageUrl"] = downloadUrl
                    }
                    reviewRef.update(updates).await()
                    onSuccess()
                } else {
                    onFailure("Review not found")
                }
            } catch (e: Exception) {
                onFailure(e.message ?: "Failed to update review")
            }
        }
    }

    fun deleteReview(
        reviewId: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val reviewRef = firestore.collection("reviews").document(reviewId)
                reviewRef.delete().await()
                // Optionally delete the image from storage
                val imageRef = storageReference.child("reviewImages/$reviewId.jpg")
                imageRef.delete().await()
                onSuccess()
            } catch (e: Exception) {
                onFailure(e.message ?: "Failed to delete review")
            }
        }
    }
}