package com.example.carguru.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.carguru.data.local.ReviewEntity
import com.example.carguru.data.repository.ReviewRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID

class AddReviewViewModel(private val reviewRepository: ReviewRepository) : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val storageReference = FirebaseStorage.getInstance().reference

    fun addReview(
        title: String,
        manufacturer: String,
        model: String,
        year: String,
        trim: String,
        rating: Int,
        reviewText: String,
        imageUri: Uri?,
        callback: (String?) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val userId = auth.currentUser?.uid
                if (userId != null) {
                    val reviewId = UUID.randomUUID().toString()
                    if (imageUri != null) {
                        val imageRef = storageReference.child("reviewImages/$reviewId.jpg")
                        imageRef.putFile(imageUri).addOnSuccessListener {
                            imageRef.downloadUrl.addOnSuccessListener { imageUrl ->
                                viewModelScope.launch {
                                    saveReview(
                                        reviewId,
                                        title,
                                        manufacturer,
                                        model,
                                        year,
                                        trim,
                                        rating,
                                        reviewText,
                                        userId,
                                        imageUrl.toString(),
                                        callback
                                    )
                                }
                            }.addOnFailureListener { exception ->
                                callback(exception.message)
                            }
                        }.addOnFailureListener { exception ->
                            callback(exception.message)
                        }
                    } else {
                        saveReview(
                            reviewId,
                            title,
                            manufacturer,
                            model,
                            year,
                            trim,
                            rating,
                            reviewText,
                            userId,
                            null,
                            callback
                        )
                    }
                }
            } catch (e: Exception) {
                callback(e.message)
            }
        }
    }

    private suspend fun saveReview(
        reviewId: String,
        title: String,
        manufacturer: String,
        model: String,
        year: String,
        trim: String,
        rating: Int,
        reviewText: String,
        userId: String,
        imageUrl: String?,
        callback: (String?) -> Unit
    ) {
        val newReview = ReviewEntity(
            id = reviewId,
            title = title,
            manufacturer = manufacturer,
            model = model,
            year = year,
            trim = trim,
            rating = rating,
            text = reviewText,
            userId = userId,
            imageUrl = imageUrl,
            timestamp = Date(),
            lastUpdated = Date()
        )
        try {
            reviewRepository.saveReview(newReview)
            callback(null)
        } catch (e: Exception) {
            callback(e.message)
        }
    }
}
