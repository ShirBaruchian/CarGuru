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

    private var _reviews = MutableStateFlow<List<ReviewWithUser>>(emptyList())
    private var _filteredReviews = MutableStateFlow<List<ReviewWithUser>>(emptyList())
    var reviews: StateFlow<List<ReviewWithUser>> = _filteredReviews.asStateFlow()

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
                val matchesYear = year?.let { it == review.review.year } ?: true
                val matchesMake = make?.let { it == review.review.manufacturer } ?: true
                val matchesModel = model?.let { it == review.review.model } ?: true
                val matchesTrim = trim?.let { it == review.review.trim } ?: true
                matchesYear && matchesMake && matchesModel && matchesTrim
            }
        }.onEach { filteredReviews ->
            _filteredReviews.value = filteredReviews
        }.launchIn(viewModelScope)
    }

    fun fetchReviews() {
        viewModelScope.launch {
            _loading.value = true
            reviewRepository.getAllReviewsWithUser().collect { reviewList ->
                _reviews.value = reviewList
                _loading.value = false
            }
        }
    }

    fun getReview(reviewId: String): StateFlow<ReviewWithUser?> {
        return reviewRepository.getReview(reviewId)
            .stateIn(viewModelScope, SharingStarted.Lazily, null)
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
}
