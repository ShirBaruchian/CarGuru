package com.example.carguru.data.repository

import androidx.lifecycle.LiveData
import com.example.carguru.data.local.AppDatabase
import com.example.carguru.data.local.ReviewEntity
import com.example.carguru.data.remote.FirebaseReviewService
import com.example.carguru.models.Review
import com.example.carguru.utils.toReview
import com.example.carguru.utils.toReviewEntity

class ReviewRepository(private val database: AppDatabase, private val remoteService: FirebaseReviewService) {
    private val reviewDao = database.reviewDao()

    fun getAllReviews(): LiveData<List<ReviewEntity>> = reviewDao.getAllReviews()

    suspend fun insertReview(review: ReviewEntity) {
        reviewDao.insertReview(review)
        remoteService.syncReviewWithFirestore(review.toReview())
    }

    suspend fun fetchReviewsFromFirestore() {
        val reviews = remoteService.fetchReviewsFromFirestore()
        reviewDao.insertReviews(reviews.map { it.toReviewEntity() })
    }
}
