package com.example.carguru.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.carguru.models.Review
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AddReviewViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    fun addReview(
        title: String, manufacturer: String, model: String, year: String, trim: String, rating: Int, reviewText: String, callback: (String?) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val userId = auth.currentUser?.uid
                if (userId != null) {
                    val newReviewRef = firestore.collection("reviews").document()
                    val review = Review(
                        id = newReviewRef.id,
                        userId = userId,
                        title = title,
                        manufacturer = manufacturer,
                        model = model,
                        year = year,
                        trim = trim,
                        rating = rating,
                        text = reviewText
                    )
                    newReviewRef.set(review).await()
                    callback(null)
                }
            } catch (e: Exception) {
                callback(e.message)
            }
        }
    }
}
